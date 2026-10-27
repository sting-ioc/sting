package sting.processor;

import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.SuppressWarningsUtil;

final class FactoryGenerator {
    private FactoryGenerator() {}

    static TypeSpec buildType(final ProcessingEnvironment processingEnv, final FactoryDescriptor factory) {
        final TypeElement element = factory.getElement();
        final TypeMirror type = element.asType();
        final TypeSpec.Builder builder = TypeSpec.classBuilder(StingGeneratorUtil.getGeneratedClassName(element))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(ClassName.get("sting", "Injectable"))
                .addAnnotation(AnnotationSpec.builder(ClassName.get("sting", "Typed"))
                        .addMember("value", "$T.class", type)
                        .build())
                .addSuperinterface(GeneratorUtil.getTypeName(type));
        GeneratorUtil.addOriginatingTypes(element, builder);
        GeneratorUtil.copyWhitelistedAnnotations(element, builder);
        builder.addAnnotation(ClassName.get("org.jspecify.annotations", "NullMarked"));
        GeneratorUtil.addGeneratedAnnotation(processingEnv, builder, StingProcessor.class.getName());
        SuppressWarningsUtil.addSuppressWarningsIfRequired(
                processingEnv, builder, Collections.emptyList(), Collections.singletonList(type));

        emitFields(processingEnv, factory, builder);
        emitConstructor(factory, builder);
        emitMethods(processingEnv, factory, builder);

        return builder.build();
    }

    private static void emitFields(
            final ProcessingEnvironment processingEnv,
            final FactoryDescriptor factory,
            final TypeSpec.Builder builder) {
        for (final FactoryDependencyDescriptor dependency : factory.getDependencies()) {
            final ServiceRequest serviceRequest = dependency.serviceRequest();
            final ServiceSpec service = serviceRequest.getService();
            final TypeName type = StingGeneratorUtil.getServiceType(serviceRequest);
            final var field = FieldSpec.builder(
                    service.isOptional() ? StingGeneratorUtil.asNullable(type) : type,
                    dependency.fieldName(),
                    Modifier.PRIVATE,
                    Modifier.FINAL);
            final var types = Collections.singletonList(service.getCoordinate().type());
            SuppressWarningsUtil.addSuppressWarningsIfRequired(processingEnv, field, Collections.emptyList(), types);
            builder.addField(field.build());
        }
    }

    private static void emitConstructor(final FactoryDescriptor factory, final TypeSpec.Builder builder) {
        final MethodSpec.Builder ctor = MethodSpec.constructorBuilder();
        final var annotations = new ArrayList<>(GeneratorUtil.ANNOTATION_WHITELIST);
        annotations.add(Constants.NAMED_CLASSNAME);
        for (final FactoryDependencyDescriptor dependency : factory.getDependencies()) {
            final ServiceRequest serviceRequest = dependency.serviceRequest();
            final var parameterElement = (VariableElement) serviceRequest.getElement();
            final ParameterSpec.Builder parameter = ParameterSpec.builder(
                    StingGeneratorUtil.getServiceType(serviceRequest), dependency.parameterName(), Modifier.FINAL);
            GeneratorUtil.copyWhitelistedAnnotations(parameterElement, parameter, annotations);
            ctor.addParameter(parameter.build());
            ctor.addStatement("$N = $N", dependency.fieldName(), dependency.parameterName());
        }
        builder.addMethod(ctor.build());
    }

    private static void emitMethods(
            final ProcessingEnvironment processingEnv,
            final FactoryDescriptor factory,
            final TypeSpec.Builder builder) {
        for (final FactoryMethodDescriptor methodDescriptor : factory.getMethods()) {
            final MethodSpec.Builder method = MethodSpec.methodBuilder(
                            methodDescriptor.method().getSimpleName().toString())
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(GeneratorUtil.getTypeName(methodDescriptor.method().getReturnType()));
            GeneratorUtil.copyWhitelistedAnnotations(methodDescriptor.method(), method);
            for (final VariableElement parameterElement :
                    methodDescriptor.method().getParameters()) {
                final ParameterSpec.Builder parameter = ParameterSpec.builder(
                        GeneratorUtil.getTypeName(parameterElement.asType()),
                        parameterElement.getSimpleName().toString());
                GeneratorUtil.copyWhitelistedAnnotations(parameterElement, parameter);
                method.addParameter(parameter.build());
            }
            SuppressWarningsUtil.addSuppressWarningsIfRequired(
                    processingEnv,
                    method,
                    Collections.emptyList(),
                    Collections.singletonList(methodDescriptor.method().asType()));

            final var code = new StringBuilder();
            final var args = new ArrayList<Object>();
            code.append("return new $T(");
            args.add(methodDescriptor.producedType());
            final List<? extends VariableElement> constructorParameters = methodDescriptor.constructorParameters();
            final var methodParametersByIndex = methodDescriptor.methodParametersByConstructorIndex();
            final var dependenciesByIndex = methodDescriptor.dependenciesByConstructorIndex();
            for (int i = 0; i < constructorParameters.size(); i++) {
                if (0 != i) {
                    code.append(", ");
                }
                final VariableElement factoryParameter = methodParametersByIndex.get(i);
                if (null != factoryParameter) {
                    code.append("$N");
                    args.add(factoryParameter.getSimpleName().toString());
                } else {
                    final FactoryDependencyDescriptor dependency = Objects.requireNonNull(dependenciesByIndex.get(i));
                    code.append("$N");
                    args.add(dependency.fieldName());
                }
            }
            code.append(")");
            method.addStatement(code.toString(), args.toArray());
            builder.addMethod(method.build());
        }
    }
}
