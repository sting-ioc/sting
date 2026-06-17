package sting.processor;

import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
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
import org.realityforge.proton.ProcessorException;
import org.realityforge.proton.SuppressWarningsUtil;

final class InterceptorProxyGenerator
{
  private InterceptorProxyGenerator()
  {
  }

  @Nonnull
  static TypeSpec buildType( @Nonnull final ProcessingEnvironment processingEnv,
                             @Nonnull final InterceptorProxyDescriptor proxy,
                             @Nonnull final List<InterceptorCodeGenerator> plugins )
  {
    final var serviceElement =
      (TypeElement) ( (DeclaredType) proxy.getService().service().getCoordinate().type() ).asElement();
    final var builder =
      TypeSpec.classBuilder( proxy.getClassName().simpleName() )
        .addModifiers( Modifier.PUBLIC, Modifier.FINAL )
        .addSuperinterface( TypeName.get( serviceElement.asType() ) );
    GeneratorUtil.addOriginatingTypes( serviceElement, builder );
    GeneratorUtil.addGeneratedAnnotation( processingEnv, builder, StingProcessor.class.getName() );

    emitFields( builder, proxy );
    emitConstructor( builder, proxy );
    emitCreateMethod( processingEnv, builder, proxy );
    emitServiceMethods( processingEnv, builder, proxy, plugins, serviceElement );

    return builder.build();
  }

  private static void emitFields( @Nonnull final TypeSpec.Builder builder,
                                  @Nonnull final InterceptorProxyDescriptor proxy )
  {
    builder.addField( FieldSpec.builder( TypeName.get( proxy.getService().service().getCoordinate().type() ),
                                         "$sting$_target",
                                         Modifier.PRIVATE,
                                         Modifier.FINAL )
                        .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                        .build() );
    var index = 1;
    for ( final var interceptor : proxy.getService().interceptors() )
    {
      if ( interceptor.hasGenericInterceptor() )
      {
        builder.addField( FieldSpec.builder( TypeName.get( interceptor.getInterceptor().element().asType() ),
                                             interceptorFieldName( index++ ),
                                             Modifier.PRIVATE,
                                             Modifier.FINAL )
                            .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                            .build() );
      }
    }
  }

  private static void emitConstructor( @Nonnull final TypeSpec.Builder builder,
                                       @Nonnull final InterceptorProxyDescriptor proxy )
  {
    final var ctor = MethodSpec.constructorBuilder().addModifiers( Modifier.PRIVATE );
    ctor.addParameter( ParameterSpec.builder( TypeName.get( proxy.getService().service().getCoordinate().type() ),
                                              "target",
                                              Modifier.FINAL )
                         .build() );
    var index = 1;
    for ( final var interceptor : proxy.getService().interceptors() )
    {
      if ( interceptor.hasGenericInterceptor() )
      {
        ctor.addParameter( ParameterSpec.builder( TypeName.get( interceptor.getInterceptor().element().asType() ),
                                                  "interceptor" + index,
                                                  Modifier.FINAL )
                             .build() );
        index++;
      }
    }
    ctor.addStatement( "$N = $N", "$sting$_target", "target" );
    index = 1;
    for ( final var interceptor : proxy.getService().interceptors() )
    {
      if ( interceptor.hasGenericInterceptor() )
      {
        ctor.addStatement( "$N = $N", interceptorFieldName( index ), "interceptor" + index );
        index++;
      }
    }
    builder.addMethod( ctor.build() );
  }

  private static void emitCreateMethod( @Nonnull final ProcessingEnvironment processingEnv,
                                        @Nonnull final TypeSpec.Builder builder,
                                        @Nonnull final InterceptorProxyDescriptor proxy )
  {
    final var method =
      MethodSpec.methodBuilder( "create" )
        .addModifiers( Modifier.PUBLIC, Modifier.STATIC )
        .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
        .returns( Object.class )
        .addParameter( Object.class, "target", Modifier.FINAL );
    final var suppressTypes = new ArrayList<TypeMirror>();
    suppressTypes.add( proxy.getService().service().getCoordinate().type() );
    var index = 1;
    for ( final var interceptor : proxy.getService().interceptors() )
    {
      if ( interceptor.hasGenericInterceptor() )
      {
        method.addParameter( Object.class, "interceptor" + index, Modifier.FINAL );
        suppressTypes.add( interceptor.getInterceptor().element().asType() );
        index++;
      }
    }
    final var code = new StringBuilder();
    final var args = new ArrayList<>();
    code.append( "return new $T( ($T) $N" );
    args.add( proxy.getClassName() );
    args.add( proxy.getService().service().getCoordinate().type() );
    args.add( "target" );
    index = 1;
    for ( final var interceptor : proxy.getService().interceptors() )
    {
      if ( interceptor.hasGenericInterceptor() )
      {
        code.append( ", ($T) $N" );
        args.add( interceptor.getInterceptor().element().asType() );
        args.add( "interceptor" + index );
        index++;
      }
    }
    code.append( " )" );
    method.addStatement( code.toString(), args.toArray() );
    SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv,
                                                        method,
                                                        Collections.emptyList(),
                                                        suppressTypes );
    builder.addMethod( method.build() );
  }

  private static void emitServiceMethods( @Nonnull final ProcessingEnvironment processingEnv,
                                          @Nonnull final TypeSpec.Builder builder,
                                          @Nonnull final InterceptorProxyDescriptor proxy,
                                          @Nonnull final List<InterceptorCodeGenerator> plugins,
                                          @Nonnull final TypeElement serviceElement )
  {
    final var serviceType = (DeclaredType) serviceElement.asType();
    final var emitted = new HashSet<String>();
    final var methods =
      ElementsUtil.getMethods( serviceElement, processingEnv.getElementUtils(), processingEnv.getTypeUtils() );
    for ( final var method : methods )
    {
      if ( shouldProxyMethod( serviceElement, method ) )
      {
        final var signature = method.getSimpleName() + "/" + method.getParameters().size() + "/" + method.asType();
        if ( emitted.add( signature ) )
        {
          builder.addMethod( buildServiceMethod( processingEnv, proxy, plugins, serviceType, method ) );
        }
      }
    }
  }

  private static boolean shouldProxyMethod( @Nonnull final TypeElement serviceElement,
                                            @Nonnull final ExecutableElement method )
  {
    final var modifiers = method.getModifiers();
    if ( !modifiers.contains( Modifier.STATIC ) && !modifiers.contains( Modifier.PRIVATE ) )
    {
      final var element = method.getEnclosingElement();
      return !Object.class.getName().equals( ( (TypeElement) element ).getQualifiedName().toString() ) ||
             element == serviceElement;
    }
    else
    {
      return false;
    }
  }

  @Nonnull
  private static MethodSpec buildServiceMethod( @Nonnull final ProcessingEnvironment processingEnv,
                                                @Nonnull final InterceptorProxyDescriptor proxy,
                                                @Nonnull final List<InterceptorCodeGenerator> plugins,
                                                @Nonnull final DeclaredType serviceType,
                                                @Nonnull final ExecutableElement method )
  {
    final var builder = MethodSpec.overriding( method, serviceType, processingEnv.getTypeUtils() );
    final var parameters = method.getParameters();
    final var arguments = new ArgumentState( parameters, mayRequestArguments( proxy ) );
    final var voidReturn = TypeKind.VOID == method.getReturnType().getKind();
    arguments.declareIfRequired( builder );
    if ( !voidReturn )
    {
      builder.addStatement( "$T result", method.getReturnType() );
    }
    emitInterceptorBlock( builder,
                          proxy,
                          plugins,
                          method,
                          modelFor( method ),
                          0,
                          catchTypes( processingEnv, method ),
                          arguments );
    if ( !voidReturn )
    {
      builder.addStatement( "return result" );
    }
    return builder.build();
  }

  private static void emitInterceptorBlock( @Nonnull final MethodSpec.Builder builder,
                                            @Nonnull final InterceptorProxyDescriptor proxy,
                                            @Nonnull final List<InterceptorCodeGenerator> plugins,
                                            @Nonnull final ExecutableElement method,
                                            @Nonnull final InterceptedMethodModel methodModel,
                                            final int index,
                                            @Nonnull final List<TypeMirror> catchTypes,
                                            @Nonnull final ArgumentState arguments )
  {
    final var interceptors = proxy.getService().interceptors();
    if ( index == interceptors.size() )
    {
      emitTargetCall( builder, method );
    }
    else
    {
      final var interceptor = interceptors.get( index );
      emitLifecycle( builder,
                     proxy,
                     plugins,
                     method,
                     methodModel,
                     interceptor,
                     InterceptorPhase.BEFORE,
                     null,
                     arguments );
      if ( hasAfterException( interceptor ) )
      {
        builder.beginControlFlow( "try" );
        emitInterceptorBlock( builder, proxy, plugins, method, methodModel, index + 1, catchTypes, arguments );
        var first = true;
        for ( final var catchType : catchTypes )
        {
          if ( first )
          {
            builder.nextControlFlow( "catch ( $T t )", catchType );
            first = false;
          }
          else
          {
            builder.nextControlFlow( "catch ( $T t )", catchType );
          }
          emitLifecycle( builder,
                         proxy,
                         plugins,
                         method,
                         methodModel,
                         interceptor,
                         InterceptorPhase.AFTER_EXCEPTION,
                         "t",
                         arguments );
          builder.addStatement( "throw t" );
        }
        builder.endControlFlow();
      }
      else
      {
        emitInterceptorBlock( builder, proxy, plugins, method, methodModel, index + 1, catchTypes, arguments );
      }
      emitLifecycle( builder,
                     proxy,
                     plugins,
                     method,
                     methodModel,
                     interceptor,
                     InterceptorPhase.AFTER,
                     null,
                     arguments );
    }
  }

  private static boolean hasAfterException( @Nonnull final InterceptorBindingDescriptor interceptor )
  {
    return interceptor.hasGenericInterceptor() ?
           null != interceptor.getInterceptor().findMethod( InterceptorPhase.AFTER_EXCEPTION ) :
           InterceptorBindingDescriptor.ClaimState.CLAIMED == interceptor.getClaimState();
  }

  private static void emitTargetCall( @Nonnull final MethodSpec.Builder builder,
                                      @Nonnull final ExecutableElement method )
  {
    final var code = new StringBuilder();
    final var args = new ArrayList<>();
    if ( TypeKind.VOID != method.getReturnType().getKind() )
    {
      code.append( "result = " );
    }
    code.append( "$N.$N(" );
    args.add( "$sting$_target" );
    args.add( method.getSimpleName().toString() );
    final var parameters = method.getParameters();
    for ( var i = 0; i < parameters.size(); i++ )
    {
      if ( 0 != i )
      {
        code.append( ", " );
      }
      code.append( "$N" );
      args.add( parameters.get( i ).getSimpleName().toString() );
    }
    code.append( ")" );
    builder.addStatement( code.toString(), args.toArray() );
  }

  private static void emitLifecycle( @Nonnull final MethodSpec.Builder builder,
                                     @Nonnull final InterceptorProxyDescriptor proxy,
                                     @Nonnull final List<InterceptorCodeGenerator> plugins,
                                     @Nonnull final ExecutableElement method,
                                     @Nonnull final InterceptedMethodModel methodModel,
                                     @Nonnull final InterceptorBindingDescriptor interceptor,
                                     @Nonnull final InterceptorPhase phase,
                                     @Nullable final String thrownName,
                                     @Nonnull final ArgumentState arguments )
  {
    if ( interceptor.hasGenericInterceptor() )
    {
      final var lifecycleMethod = interceptor.getInterceptor().findMethod( phase );
      if ( null != lifecycleMethod )
      {
        emitGenericLifecycle( builder, proxy, method, interceptor, lifecycleMethod, thrownName, arguments );
      }
    }
    else if ( InterceptorBindingDescriptor.ClaimState.CLAIMED == interceptor.getClaimState() )
    {
      final var generator = findPlugin( plugins, interceptor );
      if ( null != generator )
      {
        final var emitter =
          new PluginLifecycleCodeEmitter( builder, method, interceptor, phase, thrownName, arguments );
        if ( InterceptorPhase.BEFORE == phase )
        {
          generator.emitBefore( methodModel, interceptor, emitter );
        }
        else if ( InterceptorPhase.AFTER == phase )
        {
          generator.emitAfter( methodModel, interceptor, emitter );
        }
        else
        {
          generator.emitAfterException( methodModel, interceptor, emitter );
        }
      }
    }
  }

  @Nullable
  private static InterceptorCodeGenerator findPlugin( @Nonnull final List<InterceptorCodeGenerator> plugins,
                                                      @Nonnull final InterceptorBindingDescriptor interceptor )
  {
    final var pluginId = interceptor.getPluginIds().get( 0 );
    for ( final var plugin : plugins )
    {
      final var canonicalName = plugin.getClass().getCanonicalName();
      final var id = null == canonicalName ? plugin.getClass().getName() : canonicalName;
      if ( pluginId.equals( id ) )
      {
        return plugin;
      }
    }
    return null;
  }

  private static void emitGenericLifecycle( @Nonnull final MethodSpec.Builder builder,
                                            @Nonnull final InterceptorProxyDescriptor proxy,
                                            @Nonnull final ExecutableElement method,
                                            @Nonnull final InterceptorBindingDescriptor interceptor,
                                            @Nonnull final InterceptorMethodDescriptor lifecycleMethod,
                                            @Nullable final String thrownName,
                                            @Nonnull final ArgumentState arguments )
  {
    final var code = new StringBuilder();
    final var args = new ArrayList<>();
    code.append( "$N.$N(" );
    args.add( fieldNameFor( proxy, interceptor ) );
    args.add( lifecycleMethod.method().getSimpleName().toString() );
    for ( var i = 0; i < lifecycleMethod.parameters().size(); i++ )
    {
      if ( 0 != i )
      {
        code.append( ", " );
      }
      appendLifecycleParameter( code,
                                args,
                                builder,
                                proxy,
                                method,
                                interceptor,
                                lifecycleMethod.parameters().get( i ),
                                thrownName,
                                arguments );
    }
    code.append( ")" );
    builder.addStatement( code.toString(), args.toArray() );
  }

  private static void appendLifecycleParameter( @Nonnull final StringBuilder code,
                                                @Nonnull final List<Object> args,
                                                @Nonnull final MethodSpec.Builder builder,
                                                @Nonnull final InterceptorProxyDescriptor proxy,
                                                @Nonnull final ExecutableElement method,
                                                @Nonnull final InterceptorBindingDescriptor interceptor,
                                                @Nonnull final LifecycleParameterDescriptor parameter,
                                                @Nullable final String thrownName,
                                                @Nonnull final ArgumentState arguments )
  {
    switch ( parameter.kind() )
    {
      case SERVICE_TYPE ->
      {
        code.append( "$S" );
        args.add( proxy.getService().service().getCoordinate().type().toString() );
      }
      case METHOD_NAME ->
      {
        code.append( "$S" );
        args.add( method.getSimpleName().toString() );
      }
      case BINDING_VALUE -> code.append( interceptor.values().get( parameter.name() ).javaLiteral() );
      case ARGUMENTS -> code.append( arguments.expression( builder ) );
      case RESULT -> code.append( TypeKind.VOID == method.getReturnType().getKind() ? "null" : "result" );
      case THROWN ->
      {
        assert null != thrownName;
        code.append( "$N" );
        args.add( thrownName );
      }
    }
  }

  @Nonnull
  private static String fieldNameFor( @Nonnull final InterceptorProxyDescriptor proxy,
                                      @Nonnull final InterceptorBindingDescriptor descriptor )
  {
    var index = 1;
    for ( final var interceptor : proxy.getService().interceptors() )
    {
      if ( interceptor == descriptor )
      {
        return interceptorFieldName( index );
      }
      else if ( interceptor.hasGenericInterceptor() )
      {
        index++;
      }
    }
    throw new IllegalStateException();
  }

  @Nonnull
  private static String interceptorFieldName( final int index )
  {
    return "$sting$_interceptor" + index;
  }

  private static boolean mayRequestArguments( @Nonnull final InterceptorProxyDescriptor proxy )
  {
    return proxy.getService().interceptors()
      .stream()
      .anyMatch( i -> !i.hasGenericInterceptor() || i.getInterceptor().requestsArguments() );
  }

  @Nonnull
  private static List<TypeMirror> catchTypes( @Nonnull final ProcessingEnvironment processingEnv,
                                              @Nonnull final ExecutableElement method )
  {
    final var types = new ArrayList<TypeMirror>();
    types.add( processingEnv.getElementUtils().getTypeElement( RuntimeException.class.getName() ).asType() );
    types.add( processingEnv.getElementUtils().getTypeElement( Error.class.getName() ).asType() );
    for ( final TypeMirror thrownType : method.getThrownTypes() )
    {
      if ( !isUncheckedThrowable( processingEnv, thrownType ) &&
           types.stream().noneMatch( t -> t.toString().equals( thrownType.toString() ) ) )
      {
        types.add( thrownType );
      }
    }
    return types;
  }

  private static boolean isUncheckedThrowable( @Nonnull final ProcessingEnvironment processingEnv,
                                               @Nonnull final TypeMirror type )
  {
    final var elementUtils = processingEnv.getElementUtils();
    final var runtimeException = elementUtils.getTypeElement( RuntimeException.class.getName() );
    final var error = elementUtils.getTypeElement( Error.class.getName() );
    final var typeUtils = processingEnv.getTypeUtils();
    return typeUtils.isAssignable( type, runtimeException.asType() ) ||
           typeUtils.isAssignable( type, error.asType() );
  }

  @Nonnull
  private static InterceptedMethodModel modelFor( @Nonnull final ExecutableElement method )
  {
    return new InterceptedMethodModelImpl( method.getSimpleName().toString(),
                                           method.getReturnType().toString(),
                                           method.getParameters().stream().map( p -> p.asType().toString() ).toList(),
                                           method.getThrownTypes().stream().map( TypeMirror::toString ).toList(),
                                           method.getModifiers().contains( Modifier.DEFAULT ),
                                           method.isVarArgs() );
  }

  private record PluginLifecycleCodeEmitter(@Nonnull MethodSpec.Builder builder, @Nonnull ExecutableElement method,
                                            @Nonnull InterceptorBindingDescriptor binding,
                                            @Nonnull InterceptorPhase phase, @Nullable String thrownName,
                                            @Nonnull ArgumentState arguments)
    implements LifecycleCodeEmitter
  {
    @Nonnull
    @Override
    public String serviceType()
    {
      return stringLiteral( binding.serviceTypeName() );
    }

    @Nonnull
    @Override
    public String methodName()
    {
      return stringLiteral( method.getSimpleName().toString() );
    }

    @Nonnull
    @Override
    public String bindingValue( @Nonnull final String name )
    {
      final var value = binding.values().get( name );
      if ( null == value )
      {
        throw pluginError( "@BindingValue references unknown interceptor binding member " + name );
      }
      else
      {
        return value.javaLiteral();
      }
    }

    @Nonnull
    @Override
    public String argument( final int index )
    {
      if ( index < 0 || index >= method.getParameters().size() )
      {
        throw pluginError( "argument(" + index + ") is outside the intercepted method parameter range" );
      }
      else
      {
        return method.getParameters().get( index ).getSimpleName().toString();
      }
    }

    @Nonnull
    @Override
    public String argumentsArray()
    {
      return arguments.expression( builder );
    }

    @Nonnull
    @Override
    public String result()
    {
      if ( InterceptorPhase.AFTER != phase )
      {
        throw pluginError( "result() is only valid during after emission" );
      }
      else
      {
        return TypeKind.VOID == method.getReturnType().getKind() ? "null" : "result";
      }
    }

    @Nonnull
    @Override
    public String thrown()
    {
      if ( InterceptorPhase.AFTER_EXCEPTION != phase )
      {
        throw pluginError( "thrown() is only valid during afterException emission" );
      }
      else
      {
        assert null != thrownName;
        return thrownName;
      }
    }

    @Override
    public void emitStatement( @Nonnull final String javaStatement )
    {
      builder.addCode( escapeCode( javaStatement ) );
      if ( !javaStatement.endsWith( "\n" ) )
      {
        builder.addCode( "\n" );
      }
    }

    @Nonnull
    private String stringLiteral( @Nonnull final String value )
    {
      return CodeBlock.of( "$S", value ).toString();
    }

    @Nonnull
    private ProcessorException pluginError( @Nonnull final String message )
    {
      return new ProcessorException( message, binding.getUsageElement(), binding.getAnnotation() );
    }
  }

  private record ArgumentState(@Nonnull List<? extends VariableElement> parameters, boolean required)
  {
    private void declareIfRequired( @Nonnull final MethodSpec.Builder builder )
    {
      if ( required )
      {
        builder.addStatement( "$T[] arguments = null", Object.class );
      }
    }

    @Nonnull
    private String expression( @Nonnull final MethodSpec.Builder builder )
    {
      assert required;
      builder.beginControlFlow( "if ( null == arguments )" );
      {
        final var code = new StringBuilder();
        final var args = new ArrayList<>();
        code.append( "arguments = new Object[] {" );
        for ( var i = 0; i < parameters.size(); i++ )
        {
          if ( 0 != i )
          {
            code.append( "," );
          }
          code.append( "$N" );
          args.add( parameters.get( i ).getSimpleName().toString() );
        }
        code.append( "}" );
        builder.addStatement( code.toString(), args.toArray() );
      }
      builder.endControlFlow();
      return "arguments";
    }
  }

  @Nonnull
  private static String escapeCode( @Nonnull final String code )
  {
    return code.replace( "$", "$$" );
  }
}
