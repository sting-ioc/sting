package sting.processor;

import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.ArrayTypeName;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.realityforge.proton.ElementsUtil;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.SuppressWarningsUtil;

final class InterceptorProxyGenerator {
    @Nonnull
    private static final String TARGET_FIELD_NAME = "_target";

    @Nonnull
    private static final String JETBRAINS_UNMODIFIABLE_CLASSNAME = "org.jetbrains.annotations.Unmodifiable";

    private InterceptorProxyGenerator() {}

    @Nonnull
    static TypeSpec buildType(
            @Nonnull final ProcessingEnvironment processingEnv, @Nonnull final InterceptorProxyDescriptor proxy) {
        final var serviceElement = (TypeElement)
                ((DeclaredType) proxy.getService().service().getCoordinate().type()).asElement();
        final var builder = TypeSpec.classBuilder(proxy.getClassName().simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(TypeName.get(serviceElement.asType()));
        GeneratorUtil.addOriginatingTypes(serviceElement, builder);
        GeneratorUtil.addGeneratedAnnotation(processingEnv, builder, StingProcessor.class.getName());

        emitFields(builder, proxy);
        emitConstructor(builder, proxy);
        emitCreateMethod(processingEnv, builder, proxy);
        emitServiceMethods(processingEnv, builder, proxy, serviceElement);

        return builder.build();
    }

    private static void emitFields(
            @Nonnull final TypeSpec.Builder builder, @Nonnull final InterceptorProxyDescriptor proxy) {
        builder.addField(FieldSpec.builder(
                        TypeName.get(
                                proxy.getService().service().getCoordinate().type()),
                        TARGET_FIELD_NAME,
                        Modifier.PRIVATE,
                        Modifier.FINAL)
                .addAnnotation(GeneratorUtil.NONNULL_CLASSNAME)
                .build());
        var index = 1;
        for (final var interceptor : proxy.getService().interceptors()) {
            builder.addField(FieldSpec.builder(
                            TypeName.get(interceptor.getInterceptor().element().asType()),
                            interceptorFieldName(index++),
                            Modifier.PRIVATE,
                            Modifier.FINAL)
                    .addAnnotation(GeneratorUtil.NONNULL_CLASSNAME)
                    .build());
        }
    }

    private static void emitConstructor(
            @Nonnull final TypeSpec.Builder builder, @Nonnull final InterceptorProxyDescriptor proxy) {
        final var ctor = MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE);
        ctor.addParameter(ParameterSpec.builder(
                        TypeName.get(
                                proxy.getService().service().getCoordinate().type()),
                        "target",
                        Modifier.FINAL)
                .build());
        var index = 1;
        for (final var interceptor : proxy.getService().interceptors()) {
            ctor.addParameter(ParameterSpec.builder(
                            TypeName.get(interceptor.getInterceptor().element().asType()),
                            "interceptor" + index,
                            Modifier.FINAL)
                    .build());
            index++;
        }
        ctor.addStatement("$N = $N", TARGET_FIELD_NAME, "target");
        final var interceptorCount = proxy.getService().interceptors().size();
        for (int i = 0; i < interceptorCount; i++) {
            index = i + 1;
            ctor.addStatement("$N = $N", interceptorFieldName(index), "interceptor" + index);
        }
        builder.addMethod(ctor.build());
    }

    private static void emitCreateMethod(
            @Nonnull final ProcessingEnvironment processingEnv,
            @Nonnull final TypeSpec.Builder builder,
            @Nonnull final InterceptorProxyDescriptor proxy) {
        final var method = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addAnnotation(GeneratorUtil.NONNULL_CLASSNAME)
                .returns(Object.class)
                .addParameter(Object.class, "target", Modifier.FINAL);
        final var suppressTypes = new ArrayList<TypeMirror>();
        suppressTypes.add(proxy.getService().service().getCoordinate().type());
        var index = 1;
        for (final var interceptor : proxy.getService().interceptors()) {
            method.addParameter(Object.class, "interceptor" + index, Modifier.FINAL);
            suppressTypes.add(interceptor.getInterceptor().element().asType());
            index++;
        }
        final var code = new StringBuilder();
        final var args = new ArrayList<>();
        code.append("return new $T( ($T) $N");
        args.add(proxy.getClassName());
        args.add(proxy.getService().service().getCoordinate().type());
        args.add("target");
        index = 1;
        for (final var interceptor : proxy.getService().interceptors()) {
            code.append(", ($T) $N");
            args.add(interceptor.getInterceptor().element().asType());
            args.add("interceptor" + index);
            index++;
        }
        code.append(" )");
        method.addStatement(code.toString(), args.toArray());
        SuppressWarningsUtil.addSuppressWarningsIfRequired(
                processingEnv, method, Collections.emptyList(), suppressTypes);
        builder.addMethod(method.build());
    }

    private static void emitServiceMethods(
            @Nonnull final ProcessingEnvironment processingEnv,
            @Nonnull final TypeSpec.Builder builder,
            @Nonnull final InterceptorProxyDescriptor proxy,
            @Nonnull final TypeElement serviceElement) {
        final var emitted = new HashSet<String>();
        final var methods =
                ElementsUtil.getMethods(serviceElement, processingEnv.getElementUtils(), processingEnv.getTypeUtils());
        final var around = hasAround(proxy);
        for (final var method : methods) {
            if (shouldProxyMethod(serviceElement, method)) {
                final var signature =
                        method.getSimpleName() + "/" + method.getParameters().size() + "/" + method.asType();
                if (emitted.add(signature)) {
                    final var invocationName = "invoke_" + method.getSimpleName();
                    builder.addMethod(
                            buildServiceMethod(processingEnv, proxy, serviceElement, method, around, invocationName));
                    if (around) {
                        emitAroundInvocationMethods(processingEnv, builder, proxy, method, invocationName);
                    }
                }
            }
        }
    }

    private static boolean shouldProxyMethod(
            @Nonnull final TypeElement serviceElement, @Nonnull final ExecutableElement method) {
        final var modifiers = method.getModifiers();
        if (!modifiers.contains(Modifier.STATIC) && !modifiers.contains(Modifier.PRIVATE)) {
            final var element = method.getEnclosingElement();
            return !Object.class
                            .getName()
                            .equals(((TypeElement) element).getQualifiedName().toString())
                    || element == serviceElement;
        } else {
            return false;
        }
    }

    @Nonnull
    private static MethodSpec buildServiceMethod(
            @Nonnull final ProcessingEnvironment processingEnv,
            @Nonnull final InterceptorProxyDescriptor proxy,
            @Nonnull final TypeElement serviceElement,
            @Nonnull final ExecutableElement method,
            final boolean around,
            @Nonnull final String invocationName) {
        final var builder = GeneratorUtil.overrideMethod(processingEnv, serviceElement, method);
        if (around) {
            emitAroundServiceMethod(builder, processingEnv, method, invocationName);
            return builder.build();
        }

        final var parameters = method.getParameters();
        final var arguments = new ArgumentState(parameters, mayRequestArguments(proxy));
        final var voidReturn = TypeKind.VOID == method.getReturnType().getKind();
        arguments.declareIfRequired(builder);
        if (!voidReturn) {
            builder.addStatement("$T result", method.getReturnType());
        }
        emitInterceptorBlock(builder, proxy, method, 0, catchTypes(processingEnv, method), arguments);
        if (!voidReturn) {
            builder.addStatement("return result");
        }
        return builder.build();
    }

    private static void emitAroundServiceMethod(
            @Nonnull final MethodSpec.Builder builder,
            @Nonnull final ProcessingEnvironment processingEnv,
            @Nonnull final ExecutableElement method,
            @Nonnull final String invocationName) {
        emitInitialArguments(builder, method.getParameters(), "arguments");
        builder.beginControlFlow("try");
        final var methodName = invocationMethodName(invocationName, false, 1);

        if (TypeKind.VOID == method.getReturnType().getKind()) {
            builder.addStatement("$N( $N )", methodName, "arguments");
        } else {
            builder.addStatement("return ($T) $N( $N )", method.getReturnType(), methodName, "arguments");
        }
        emitPublicBoundaryCatches(builder, processingEnv, method);
        builder.endControlFlow();

        final var suppressTypes = new ArrayList<TypeMirror>();
        suppressTypes.add(method.getReturnType());
        suppressTypes.addAll(
                method.getParameters().stream().map(VariableElement::asType).toList());
        SuppressWarningsUtil.addSuppressWarningsIfRequired(
                processingEnv, builder, Collections.emptyList(), suppressTypes);
    }

    private static void emitAroundInvocationMethods(
            @Nonnull final ProcessingEnvironment processingEnv,
            @Nonnull final TypeSpec.Builder builder,
            @Nonnull final InterceptorProxyDescriptor proxy,
            @Nonnull final ExecutableElement method,
            @Nonnull final String invocationName) {
        final var interceptors = proxy.getService().interceptors();
        for (var index = 0; index <= interceptors.size(); index++) {
            builder.addMethod(buildAroundInvocationMethod(processingEnv, proxy, method, invocationName, index));
        }
    }

    @Nonnull
    private static MethodSpec buildAroundInvocationMethod(
            @Nonnull final ProcessingEnvironment processingEnv,
            @Nonnull final InterceptorProxyDescriptor proxy,
            @Nonnull final ExecutableElement method,
            @Nonnull final String invocationName,
            final int index) {
        final var interceptors = proxy.getService().interceptors();
        final var isTarget = index == interceptors.size();
        final var methodName = invocationMethodName(invocationName, isTarget, index + 1);
        final var voidReturn = TypeKind.VOID == method.getReturnType().getKind();
        final var unmodifiableAnnotation = findUnmodifiableAnnotation(processingEnv);
        final var builder = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PRIVATE)
                .returns(aroundInvocationReturnType(voidReturn, unmodifiableAnnotation))
                .addException(Throwable.class)
                .addParameter(ParameterSpec.builder(
                                aroundInvocationArgumentsType(unmodifiableAnnotation), "arguments", Modifier.FINAL)
                        .addAnnotation(GeneratorUtil.NONNULL_CLASSNAME)
                        .build());
        propagateAroundInvocationNullability(builder, method);
        builder.addStatement(
                "assert null != $N && $L == $N.length",
                "arguments",
                method.getParameters().size(),
                "arguments");

        if (isTarget) {
            emitAroundTargetCall(builder, method);
        } else {
            final var interceptor = interceptors.get(index);
            final var arguments = new ArgumentState(method.getParameters(), true, "arguments");
            emitLifecycle(builder, proxy, method, interceptor, InterceptorPhase.BEFORE, null, null, null, arguments);
            if (!voidReturn) {
                builder.addStatement("final $T $N", Object.class, "result");
            }
            if (hasAfterException(interceptor)) {
                builder.beginControlFlow("try");
                emitAroundInvocationStep(
                        builder, proxy, method, interceptor, invocationName, index, arguments, voidReturn);
                builder.nextControlFlow("catch ( $T t )", Throwable.class);
                emitLifecycle(
                        builder,
                        proxy,
                        method,
                        interceptor,
                        InterceptorPhase.AFTER_EXCEPTION,
                        "t",
                        voidReturn ? null : "result",
                        null,
                        arguments);
                builder.addStatement("throw t");
                builder.endControlFlow();
            } else {
                emitAroundInvocationStep(
                        builder, proxy, method, interceptor, invocationName, index, arguments, voidReturn);
            }
            emitLifecycle(
                    builder,
                    proxy,
                    method,
                    interceptor,
                    InterceptorPhase.AFTER,
                    null,
                    voidReturn ? null : "result",
                    null,
                    arguments);
            if (!voidReturn) {
                builder.addStatement("return $N", "result");
            }
        }

        final var suppressTypes = new ArrayList<TypeMirror>();
        suppressTypes.add(method.getReturnType());
        suppressTypes.addAll(
                method.getParameters().stream().map(VariableElement::asType).toList());
        SuppressWarningsUtil.addSuppressWarningsIfRequired(
                processingEnv, builder, Collections.emptyList(), suppressTypes);
        return builder.build();
    }

    @Nullable
    private static AnnotationSpec findUnmodifiableAnnotation(@Nonnull final ProcessingEnvironment processingEnv) {
        final var element = processingEnv.getElementUtils().getTypeElement(JETBRAINS_UNMODIFIABLE_CLASSNAME);
        return null == element
                ? null
                : AnnotationSpec.builder(ClassName.get(element)).build();
    }

    @Nonnull
    private static TypeName aroundInvocationReturnType(
            final boolean voidReturn, @Nullable final AnnotationSpec unmodifiableAnnotation) {
        if (voidReturn) {
            return TypeName.VOID;
        } else {
            return null == unmodifiableAnnotation
                    ? ClassName.OBJECT
                    : ClassName.OBJECT.annotated(unmodifiableAnnotation);
        }
    }

    @Nonnull
    private static TypeName aroundInvocationArgumentsType(@Nullable final AnnotationSpec unmodifiableAnnotation) {
        final TypeName componentType =
                null == unmodifiableAnnotation ? ClassName.OBJECT : ClassName.OBJECT.annotated(unmodifiableAnnotation);
        return ArrayTypeName.of(componentType);
    }

    private static void propagateAroundInvocationNullability(
            @Nonnull final MethodSpec.Builder builder, @Nonnull final ExecutableElement method) {
        final var returnType = method.getReturnType();
        if (TypeKind.VOID != returnType.getKind()) {
            if (returnType.getKind().isPrimitive()) {
                builder.addAnnotation(GeneratorUtil.NONNULL_CLASSNAME);
            } else {
                final var nonnull = GeneratorUtil.NONNULL_CLASSNAME.canonicalName();
                final var nullable = GeneratorUtil.NULLABLE_CLASSNAME.canonicalName();
                for (final var annotation : method.getAnnotationMirrors()) {
                    final var annotationType = annotation.getAnnotationType().toString();
                    if (nonnull.equals(annotationType) || nullable.equals(annotationType)) {
                        builder.addAnnotation(AnnotationSpec.get(annotation));
                    }
                }
            }
        }
    }

    private static void emitAroundInvocationStep(
            @Nonnull final MethodSpec.Builder builder,
            @Nonnull final InterceptorProxyDescriptor proxy,
            @Nonnull final ExecutableElement method,
            @Nonnull final InterceptorBindingDescriptor interceptor,
            @Nonnull final String invocationName,
            final int index,
            @Nonnull final ArgumentState arguments,
            final boolean voidReturn) {
        final var aroundMethod = interceptor.getInterceptor().findMethod(InterceptorPhase.AROUND);
        final var nextIndex = index + 1;
        final var nextIsTarget = nextIndex == proxy.getService().interceptors().size();
        final var nextMethod = invocationMethodName(invocationName, nextIsTarget, nextIndex + 1);
        if (null == aroundMethod) {
            if (voidReturn) {
                builder.addStatement("$N( $N )", nextMethod, "arguments");
            } else {
                emitResultAssignment(builder, method, "result", "$N( $N )", nextMethod, "arguments");
            }
        } else {
            final var invocationType = ClassName.bestGuess(Constants.INTERCEPTOR_INVOCATION_CLASSNAME);
            if (voidReturn) {
                builder.addStatement(
                        "final $T $N = new $T( $N -> { $N( $N ); return null; }, $N )",
                        invocationType,
                        "invocation",
                        invocationType,
                        "nextArguments",
                        nextMethod,
                        "nextArguments",
                        "arguments");
            } else {
                builder.addStatement(
                        "final $T $N = new $T( $N -> $N( $N ), $N )",
                        invocationType,
                        "invocation",
                        invocationType,
                        "nextArguments",
                        nextMethod,
                        "nextArguments",
                        "arguments");
            }
            final var call = lifecycleCall(
                    builder, proxy, method, interceptor, aroundMethod, null, null, "invocation", arguments);
            if (voidReturn) {
                builder.addStatement(call.code(), call.args().toArray());
            } else {
                emitResultAssignment(
                        builder, method, "result", call.code(), call.args().toArray());
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static void emitResultAssignment(
            @Nonnull final MethodSpec.Builder builder,
            @Nonnull final ExecutableElement method,
            @Nonnull final String resultName,
            @Nonnull final String expression,
            @Nonnull final Object... expressionArgs) {
        final var args = new ArrayList<>();
        args.add(resultName);
        args.add(method.getReturnType());
        Collections.addAll(args, expressionArgs);
        builder.addStatement("$N = ($T) " + expression, args.toArray());
    }

    private static void emitAroundTargetCall(
            @Nonnull final MethodSpec.Builder builder, @Nonnull final ExecutableElement method) {
        final var code = new StringBuilder();
        final var args = new ArrayList<>();
        if (TypeKind.VOID == method.getReturnType().getKind()) {
            code.append("$N.$N(");
        } else {
            code.append("return $N.$N(");
        }
        args.add(TARGET_FIELD_NAME);
        args.add(method.getSimpleName().toString());
        appendActiveArgumentList(code, args, method);
        code.append(")");
        builder.addStatement(code.toString(), args.toArray());
    }

    private static void appendActiveArgumentList(
            @Nonnull final StringBuilder code,
            @Nonnull final List<Object> args,
            @Nonnull final ExecutableElement method) {
        final var parameters = method.getParameters();
        for (var i = 0; i < parameters.size(); i++) {
            if (0 != i) {
                code.append(", ");
            }
            code.append("($T) $N[$L]");
            args.add(parameters.get(i).asType());
            args.add("arguments");
            args.add(i);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static void emitInitialArguments(
            @Nonnull final MethodSpec.Builder builder,
            @Nonnull final List<? extends VariableElement> parameters,
            @Nonnull final String argumentsName) {
        if (parameters.isEmpty()) {
            builder.addStatement("final $T[] $N = new $T[0]", Object.class, argumentsName, Object.class);
        } else {
            final var code = new StringBuilder();
            final var args = new ArrayList<>();
            code.append("final $T[] $N = new $T[] {");
            args.add(Object.class);
            args.add(argumentsName);
            args.add(Object.class);
            for (var i = 0; i < parameters.size(); i++) {
                if (0 != i) {
                    code.append(", ");
                }
                code.append("$N");
                args.add(parameters.get(i).getSimpleName().toString());
            }
            code.append("}");
            builder.addStatement(code.toString(), args.toArray());
        }
    }

    private static void emitPublicBoundaryCatches(
            @Nonnull final MethodSpec.Builder builder,
            @Nonnull final ProcessingEnvironment processingEnv,
            @Nonnull final ExecutableElement method) {
        final var catchTypes = catchTypes(processingEnv, method);
        for (final var catchType : catchTypes) {
            builder.nextControlFlow("catch ( $T t )", catchType);
            builder.addStatement("throw t");
        }
        if (catchTypes.stream().noneMatch(t -> Throwable.class.getName().equals(t.toString()))) {
            builder.nextControlFlow("catch ( $T t )", Throwable.class);
            builder.addStatement("throw new $T( t )", UndeclaredThrowableException.class);
        }
    }

    private static void emitInterceptorBlock(
            @Nonnull final MethodSpec.Builder builder,
            @Nonnull final InterceptorProxyDescriptor proxy,
            @Nonnull final ExecutableElement method,
            final int index,
            @Nonnull final List<TypeMirror> catchTypes,
            @Nonnull final ArgumentState arguments) {
        final var interceptors = proxy.getService().interceptors();
        if (index == interceptors.size()) {
            emitTargetCall(builder, method);
        } else {
            final var interceptor = interceptors.get(index);
            emitLifecycle(builder, proxy, method, interceptor, InterceptorPhase.BEFORE, null, arguments);
            if (hasAfterException(interceptor)) {
                builder.beginControlFlow("try");
                emitInterceptorBlock(builder, proxy, method, index + 1, catchTypes, arguments);
                var first = true;
                for (final var catchType : catchTypes) {
                    if (first) {
                        builder.nextControlFlow("catch ( $T t )", catchType);
                        first = false;
                    } else {
                        builder.nextControlFlow("catch ( $T t )", catchType);
                    }
                    emitLifecycle(
                            builder, proxy, method, interceptor, InterceptorPhase.AFTER_EXCEPTION, "t", arguments);
                    builder.addStatement("throw t");
                }
                builder.endControlFlow();
            } else {
                emitInterceptorBlock(builder, proxy, method, index + 1, catchTypes, arguments);
            }
            emitLifecycle(builder, proxy, method, interceptor, InterceptorPhase.AFTER, null, arguments);
        }
    }

    private static boolean hasAfterException(@Nonnull final InterceptorBindingDescriptor interceptor) {
        return null != interceptor.getInterceptor().findMethod(InterceptorPhase.AFTER_EXCEPTION);
    }

    private static boolean hasAround(@Nonnull final InterceptorProxyDescriptor proxy) {
        return proxy.getService().interceptors().stream().anyMatch(InterceptorProxyGenerator::hasAround);
    }

    private static boolean hasAround(@Nonnull final InterceptorBindingDescriptor interceptor) {
        return null != interceptor.getInterceptor().findMethod(InterceptorPhase.AROUND);
    }

    @Nonnull
    private static String invocationMethodName(
            @Nonnull final String invocationName, final boolean isTarget, final int index) {
        return invocationName + "_" + (isTarget ? "target" : "interceptor" + (index + 1));
    }

    private static void emitTargetCall(
            @Nonnull final MethodSpec.Builder builder, @Nonnull final ExecutableElement method) {
        final var code = new StringBuilder();
        final var args = new ArrayList<>();
        if (TypeKind.VOID != method.getReturnType().getKind()) {
            code.append("result = ");
        }
        code.append("$N.$N(");
        args.add(TARGET_FIELD_NAME);
        args.add(method.getSimpleName().toString());
        final var parameters = method.getParameters();
        for (var i = 0; i < parameters.size(); i++) {
            if (0 != i) {
                code.append(", ");
            }
            code.append("$N");
            args.add(parameters.get(i).getSimpleName().toString());
        }
        code.append(")");
        builder.addStatement(code.toString(), args.toArray());
    }

    private static void emitLifecycle(
            @Nonnull final MethodSpec.Builder builder,
            @Nonnull final InterceptorProxyDescriptor proxy,
            @Nonnull final ExecutableElement method,
            @Nonnull final InterceptorBindingDescriptor interceptor,
            @Nonnull final InterceptorPhase phase,
            @Nullable final String thrownName,
            @Nonnull final ArgumentState arguments) {
        emitLifecycle(builder, proxy, method, interceptor, phase, thrownName, "result", null, arguments);
    }

    @SuppressWarnings("SameParameterValue")
    private static void emitLifecycle(
            @Nonnull final MethodSpec.Builder builder,
            @Nonnull final InterceptorProxyDescriptor proxy,
            @Nonnull final ExecutableElement method,
            @Nonnull final InterceptorBindingDescriptor interceptor,
            @Nonnull final InterceptorPhase phase,
            @Nullable final String thrownName,
            @Nullable final String resultName,
            @Nullable final String proceedName,
            @Nonnull final ArgumentState arguments) {
        final var lifecycleMethod = interceptor.getInterceptor().findMethod(phase);
        if (null != lifecycleMethod) {
            emitGenericLifecycle(
                    builder,
                    proxy,
                    method,
                    interceptor,
                    lifecycleMethod,
                    thrownName,
                    resultName,
                    proceedName,
                    arguments);
        }
    }

    private static void emitGenericLifecycle(
            @Nonnull final MethodSpec.Builder builder,
            @Nonnull final InterceptorProxyDescriptor proxy,
            @Nonnull final ExecutableElement method,
            @Nonnull final InterceptorBindingDescriptor interceptor,
            @Nonnull final InterceptorMethodDescriptor lifecycleMethod,
            @Nullable final String thrownName,
            @Nullable final String resultName,
            @Nullable final String proceedName,
            @Nonnull final ArgumentState arguments) {
        final var call = lifecycleCall(
                builder, proxy, method, interceptor, lifecycleMethod, thrownName, resultName, proceedName, arguments);
        builder.addStatement(call.code(), call.args().toArray());
    }

    @Nonnull
    private static CodeFragment lifecycleCall(
            @Nonnull final MethodSpec.Builder builder,
            @Nonnull final InterceptorProxyDescriptor proxy,
            @Nonnull final ExecutableElement method,
            @Nonnull final InterceptorBindingDescriptor interceptor,
            @Nonnull final InterceptorMethodDescriptor lifecycleMethod,
            @Nullable final String thrownName,
            @Nullable final String resultName,
            @Nullable final String proceedName,
            @Nonnull final ArgumentState arguments) {
        final var code = new StringBuilder();
        final var args = new ArrayList<>();
        code.append("$N.$N(");
        args.add(fieldNameFor(proxy, interceptor));
        args.add(lifecycleMethod.method().getSimpleName().toString());
        for (var i = 0; i < lifecycleMethod.parameters().size(); i++) {
            if (0 != i) {
                code.append(", ");
            }
            appendLifecycleParameter(
                    code,
                    args,
                    builder,
                    proxy,
                    method,
                    interceptor,
                    lifecycleMethod.parameters().get(i),
                    thrownName,
                    resultName,
                    proceedName,
                    arguments);
        }
        code.append(")");
        return new CodeFragment(code.toString(), args);
    }

    private static void appendLifecycleParameter(
            @Nonnull final StringBuilder code,
            @Nonnull final List<Object> args,
            @Nonnull final MethodSpec.Builder builder,
            @Nonnull final InterceptorProxyDescriptor proxy,
            @Nonnull final ExecutableElement method,
            @Nonnull final InterceptorBindingDescriptor interceptor,
            @Nonnull final LifecycleParameterDescriptor parameter,
            @Nullable final String thrownName,
            @Nullable final String resultName,
            @Nullable final String proceedName,
            @Nonnull final ArgumentState arguments) {
        switch (parameter.kind()) {
            case SERVICE_TYPE -> {
                code.append("$S");
                args.add(proxy.getService().service().getCoordinate().type().toString());
            }
            case METHOD_NAME -> {
                code.append("$S");
                args.add(method.getSimpleName().toString());
            }
            case BINDING_VALUE ->
                code.append(interceptor.values().get(parameter.name()).javaLiteral());
            case ARGUMENTS -> arguments.appendExpression(code, args, builder);
            case PROCEED -> {
                assert null != proceedName;
                code.append("$N");
                args.add(proceedName);
            }
            case RESULT -> {
                if (TypeKind.VOID == method.getReturnType().getKind()) {
                    code.append("null");
                } else {
                    assert null != resultName;
                    code.append("$N");
                    args.add(resultName);
                }
            }
            case THROWN -> {
                assert null != thrownName;
                code.append("$N");
                args.add(thrownName);
            }
        }
    }

    @Nonnull
    private static String fieldNameFor(
            @Nonnull final InterceptorProxyDescriptor proxy, @Nonnull final InterceptorBindingDescriptor descriptor) {
        var index = 1;
        for (final var interceptor : proxy.getService().interceptors()) {
            if (interceptor == descriptor) {
                return interceptorFieldName(index);
            } else {
                index++;
            }
        }
        throw new IllegalStateException();
    }

    @Nonnull
    private static String interceptorFieldName(final int index) {
        return "_interceptor" + index;
    }

    private static boolean mayRequestArguments(@Nonnull final InterceptorProxyDescriptor proxy) {
        return proxy.getService().interceptors().stream()
                .anyMatch(i -> i.getInterceptor().requestsArguments());
    }

    @Nonnull
    private static List<TypeMirror> catchTypes(
            @Nonnull final ProcessingEnvironment processingEnv, @Nonnull final ExecutableElement method) {
        final var types = new ArrayList<TypeMirror>();
        types.add(processingEnv
                .getElementUtils()
                .getTypeElement(RuntimeException.class.getName())
                .asType());
        types.add(processingEnv
                .getElementUtils()
                .getTypeElement(Error.class.getName())
                .asType());
        for (final TypeMirror thrownType : method.getThrownTypes()) {
            if (!isUncheckedThrowable(processingEnv, thrownType)
                    && types.stream().noneMatch(t -> t.toString().equals(thrownType.toString()))) {
                types.add(thrownType);
            }
        }
        return types;
    }

    private static boolean isUncheckedThrowable(
            @Nonnull final ProcessingEnvironment processingEnv, @Nonnull final TypeMirror type) {
        final var elementUtils = processingEnv.getElementUtils();
        final var runtimeException = elementUtils.getTypeElement(RuntimeException.class.getName());
        final var error = elementUtils.getTypeElement(Error.class.getName());
        final var typeUtils = processingEnv.getTypeUtils();
        return typeUtils.isAssignable(type, runtimeException.asType()) || typeUtils.isAssignable(type, error.asType());
    }

    private record CodeFragment(
            @Nonnull String code, @Nonnull List<Object> args) {}

    private record ArgumentState(
            @Nonnull List<? extends VariableElement> parameters,
            boolean required,
            @Nullable String activeArgumentsName) {
        private ArgumentState(@Nonnull final List<? extends VariableElement> parameters, final boolean required) {
            this(parameters, required, null);
        }

        private void declareIfRequired(@Nonnull final MethodSpec.Builder builder) {
            if (required && null == activeArgumentsName) {
                builder.addStatement("$T[] arguments = null", Object.class);
            }
        }

        private void appendExpression(
                @Nonnull final StringBuilder code,
                @Nonnull final List<Object> args,
                @Nonnull final MethodSpec.Builder builder) {
            assert required;
            if (null != activeArgumentsName) {
                code.append("$N");
                args.add(activeArgumentsName);
                return;
            }
            builder.beginControlFlow("if ( null == arguments )");
            {
                final var initCode = new StringBuilder();
                final var initArgs = new ArrayList<>();
                initCode.append("arguments = new Object[] {");
                for (var i = 0; i < parameters.size(); i++) {
                    if (0 != i) {
                        initCode.append(",");
                    }
                    initCode.append("$N");
                    initArgs.add(parameters.get(i).getSimpleName().toString());
                }
                initCode.append("}");
                builder.addStatement(initCode.toString(), initArgs.toArray());
            }
            builder.endControlFlow();
            code.append("arguments");
        }
    }
}
