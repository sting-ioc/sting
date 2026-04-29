package sting.processor;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import org.realityforge.proton.ElementsUtil;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.SuppressWarningsUtil;

final class InjectorGenerator
{
  @Nonnull
  private static final ClassName DO_NOT_INLINE = ClassName.get( "javaemul.internal.annotations", "DoNotInline" );
  @Nonnull
  private static final ClassName OBJECT = ClassName.get( "java.lang", "Object" );

  private InjectorGenerator()
  {
  }

  @Nonnull
  static TypeSpec buildType( @Nonnull final ProcessingEnvironment processingEnv, @Nonnull final ComponentGraph graph )
  {
    final InjectorDescriptor injector = graph.getInjector();
    final TypeElement element = injector.getElement();
    final TypeSpec.Builder builder =
      TypeSpec
        .classBuilder( StingGeneratorUtil.getGeneratedClassName( element ) )
        .addModifiers( Modifier.FINAL );
    GeneratorUtil.addOriginatingTypes( element, builder );
    GeneratorUtil.copyWhitelistedAnnotations( element, builder );
    GeneratorUtil.addGeneratedAnnotation( processingEnv, builder, StingProcessor.class.getName() );

    builder.addSuperinterface( TypeName.get( element.asType() ) );

    emitFragmentFields( processingEnv, builder, graph );
    emitNodeFields( processingEnv, graph, builder );
    emitOutputCollectionFields( graph, builder );
    emitConstructor( graph, builder );
    emitNodeAccessorMethod( processingEnv, graph, builder );
    emitOutputMethods( processingEnv, graph, builder );

    return builder.build();
  }

  private static void emitOutputMethods( @Nonnull final ProcessingEnvironment processingEnv,
                                         @Nonnull final ComponentGraph graph,
                                         @Nonnull final TypeSpec.Builder builder )
  {
    for ( final Edge edge : graph.getRootNode().getDependsOn() )
    {
      final ServiceRequest service = edge.getServiceRequest();
      final ExecutableElement element = (ExecutableElement) service.getElement();
      final MethodSpec.Builder method =
        MethodSpec.overriding( element,
                               (DeclaredType) graph.getInjector().getElement().asType(),
                               processingEnv.getTypeUtils() );
      GeneratorUtil.copyWhitelistedAnnotations( element, method );
      SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv,
                                                          method,
                                                          Collections.emptyList(),
                                                          Collections.singletonList( element.asType() ) );
      if ( service.getKind().isCollection() )
      {
        final String name = getOutputCollectionCacheName( edge );
        final CodeBlock.Builder block = CodeBlock.builder();
        block.beginControlFlow( "if ( null == $N )", name );
        final StringBuilder code = new StringBuilder();
        final List<Object> args = new ArrayList<>();
        code.append( "$N = " );
        args.add( name );
        emitServiceValue( edge, true, code, args );
        block.addStatement( code.toString(), args.toArray() );
        block.endControlFlow();
        method.addCode( block.build() );
        method.addStatement( "return $N", name );
      }
      else
      {
        final StringBuilder code = new StringBuilder();
        final List<Object> args = new ArrayList<>();
        code.append( "return " );
        emitServiceValue( edge, true, code, args );
        method.addStatement( code.toString(), args.toArray() );
      }
      builder.addMethod( method.build() );
    }
  }

  private static void emitConstructor( @Nonnull final ComponentGraph graph, @Nonnull final TypeSpec.Builder builder )
  {
    final MethodSpec.Builder ctor = MethodSpec.constructorBuilder();

    for ( final InputDescriptor input : graph.getInjector().getInputs() )
    {
      final ParameterSpec.Builder parameter =
        ParameterSpec
          .builder( TypeName.get( input.getService().getCoordinate().getType() ),
                    input.getName(),
                    Modifier.FINAL );

      if ( !input.getService().getCoordinate().getType().getKind().isPrimitive() )
      {
        parameter.addAnnotation( input.getService().isOptional() ?
                                 GeneratorUtil.NULLABLE_CLASSNAME :
                                 GeneratorUtil.NONNULL_CLASSNAME );
      }
      ctor.addParameter( parameter.build() );
    }

    for ( final Node node : graph.getNodes() )
    {
      if ( node.isEager() )
      {
        final Binding binding = node.getBinding();
        if ( Binding.Kind.INPUT == binding.getKind() )
        {
          final InputDescriptor input = (InputDescriptor) binding.getOwner();
          final ServiceSpec serviceSpec = binding.getPublishedServices().get( 0 );
          if ( serviceSpec.isOptional() || serviceSpec.getCoordinate().getType().getKind().isPrimitive() )
          {
            ctor.addStatement( "$N = $N", node.getName(), input.getName() );
          }
          else
          {
            ctor.addStatement( "$N = $T.requireNonNull( $N )", node.getName(), Objects.class, input.getName() );
          }
        }
        else
        {
          final StringBuilder code = new StringBuilder();
          final List<Object> args = new ArrayList<>();
          provideAndAssign( node, code, args );
          ctor.addStatement( code.toString(), args.toArray() );
        }
      }
    }

    builder.addMethod( ctor.build() );
  }

  private static void emitNodeFields( @Nonnull final ProcessingEnvironment processingEnv,
                                      @Nonnull final ComponentGraph graph,
                                      @Nonnull final TypeSpec.Builder builder )
  {
    for ( final Node node : graph.getNodes() )
    {
      final FieldSpec.Builder field =
        FieldSpec
          .builder( getPublicTypeName( node ), node.getName(), Modifier.PRIVATE );
      if ( !node.getType().getKind().isPrimitive() )
      {
        field.addAnnotation( node.isEager() && node.getBinding().isRequired() ?
                             GeneratorUtil.NONNULL_CLASSNAME :
                             GeneratorUtil.NULLABLE_CLASSNAME );
      }
      if ( node.isPublic() )
      {
        SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv,
                                                            field,
                                                            Collections.emptyList(),
                                                            Collections.singletonList( node.getType() ) );
      }
      if ( node.isEager() )
      {
        field.addModifiers( Modifier.FINAL );
      }

      builder.addField( field.build() );

      if ( !node.isEager() && ( node.getBinding().isOptional() || node.getType().getKind().isPrimitive() ) )
      {
        builder.addField( FieldSpec.builder( TypeName.BOOLEAN, getFlagFieldName( node ), Modifier.PRIVATE ).build() );
      }
    }
  }

  private static void emitOutputCollectionFields( @Nonnull final ComponentGraph graph,
                                                  @Nonnull final TypeSpec.Builder builder )
  {
    for ( final Edge edge : graph.getRootNode().getDependsOn() )
    {
      final ServiceRequest serviceRequest = edge.getServiceRequest();
      if ( serviceRequest.getKind().isCollection() )
      {
        final TypeName serviceType = StingGeneratorUtil.getServiceType( serviceRequest );
        builder.addField( FieldSpec.builder( serviceType, getOutputCollectionCacheName( edge ), Modifier.PRIVATE )
                            .build() );
      }
    }
  }

  @Nonnull
  private static String getOutputCollectionCacheName( @Nonnull final Edge edge )
  {
    return StingGeneratorUtil.FRAMEWORK_PREFIX +
           edge.getServiceRequest().getElement().getSimpleName().toString() +
           "Cache";
  }

  private static TypeName getPublicTypeName( @Nonnull final Node node )
  {
    return node.isPublic() ? TypeName.get( node.getType() ) : OBJECT;
  }

  private static void emitNodeAccessorMethod( @Nonnull final ProcessingEnvironment processingEnv,
                                              @Nonnull final ComponentGraph graph,
                                              @Nonnull final TypeSpec.Builder builder )
  {
    for ( final Node node : graph.getNodes() )
    {
      if ( !node.isEager() )
      {
        final MethodSpec.Builder method =
          MethodSpec
            .methodBuilder( node.getName() )
            .addModifiers( Modifier.PRIVATE, Modifier.SYNCHRONIZED )
            .returns( getPublicTypeName( node ) );
        final Binding binding = node.getBinding();
        final Element element = binding.getElement();
        final List<TypeMirror> types = Collections.singletonList( element.getEnclosingElement().asType() );
        final List<String> additionalSuppressions = new ArrayList<>();
        if ( ElementsUtil.isDeprecated( element ) )
        {
          additionalSuppressions.add( "deprecation" );
        }
        SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv, method, additionalSuppressions, types );

        final boolean isNonnull = binding.isRequired();
        final boolean isNonPrimitive = !node.getType().getKind().isPrimitive();
        if ( isNonPrimitive )
        {
          method.addAnnotation( isNonnull ? GeneratorUtil.NONNULL_CLASSNAME : GeneratorUtil.NULLABLE_CLASSNAME );
        }

        if ( graph.getInjector().isGwt() )
        {
          // We avoid inlining as each lazy node accessor is typically a candidate for inlining and
          // will be inlined which often results the entire transitive tree of lazy node accessors
          // being inlined in successive passes. Then there will be too much code to run some of the
          // other optimization passes such as DFA as the code size is too big. Without this annotation,
          // there was one scenario where a piece of code was 4KB with @DonNotInline present and 1MB+
          // without the annotation being present.
          method.addAnnotation( DO_NOT_INLINE );
        }

        final CodeBlock.Builder block = CodeBlock.builder();
        if ( isNonnull && isNonPrimitive )
        {
          block.beginControlFlow( "if ( null == $N )", node.getName() );
        }
        else
        {
          final String flagName = getFlagFieldName( node );
          block.beginControlFlow( "if ( !$N )", flagName );
          block.addStatement( "$N = true", flagName );
        }
        final StringBuilder code = new StringBuilder();
        final List<Object> args = new ArrayList<>();
        provideAndAssign( node, code, args );
        block.addStatement( code.toString(), args.toArray() );
        block.endControlFlow();
        method.addCode( block.build() );
        if ( isNonnull && isNonPrimitive )
        {
          method.addStatement( "assert null != $N", node.getName() );
        }
        method.addStatement( "return $N", node.getName() );

        builder.addMethod( method.build() );
      }
    }
  }

  @Nonnull
  private static String getFlagFieldName( @Nonnull final Node node )
  {
    return node.getName() + "_allocated";
  }

  private static void provideAndAssign( @Nonnull final Node node,
                                        @Nonnull final StringBuilder code,
                                        @Nonnull final List<Object> args )
  {
    code.append( "$N = " );
    args.add( node.getName() );
    final boolean isNonnull = node.getBinding().isRequired();
    final boolean requireNonNull = isNonnull && !node.getType().getKind().isPrimitive();
    if ( requireNonNull )
    {
      code.append( "$T.requireNonNull( " );
      args.add( Objects.class );
    }
    if ( node.isFromProvides() )
    {
      code.append( "$N.$N" );
      args.add( node.getFragment().getName() );
      args.add( StingGeneratorUtil.getFragmentProvidesStubName( (ExecutableElement) node.getBinding().getElement() ) );
    }
    else
    {
      final InjectableDescriptor injectable = (InjectableDescriptor) node.getBinding().getOwner();
      code.append( "$T.create" );
      args.add( StingGeneratorUtil.getGeneratedClassName( injectable.getElement() ) );
    }
    code.append( '(' );
    boolean firstParam = true;
    for ( final Edge edge : node.getDependsOn() )
    {
      if ( !firstParam )
      {
        code.append( ", " );
      }
      else
      {
        firstParam = false;
      }
      emitServiceValue( edge, false, code, args );
    }
    code.append( ')' );
    if ( requireNonNull )
    {
      code.append( " )" );
    }
  }

  private static void emitServiceValue( @Nonnull final Edge edge,
                                        final boolean isOutput,
                                        @Nonnull final StringBuilder code,
                                        @Nonnull final List<Object> args )
  {
    final Collection<Node> satisfiedBy = edge.getSatisfiedBy();
    final ServiceRequest service = edge.getServiceRequest();
    final ServiceRequest.Kind kind = service.getKind();
    if ( !kind.isCollection() )
    {
      if ( satisfiedBy.isEmpty() )
      {
        emitAbsentServiceValue( service, code, args );
      }
      else
      {
        emitNodeAccessor( service, satisfiedBy.iterator().next(), isOutput, code, args );
      }
    }
    else
    {
      final int count = satisfiedBy.size();
      if ( 0 == count )
      {
        code.append( "$T.emptyList()" );
        args.add( Collections.class );
      }
      else if ( ServiceRequest.Kind.COLLECTION == kind &&
                satisfiedBy.stream().anyMatch( node -> node.getBinding().isOptional() ) )
      {
        code.append( "$T.asList( " );
        args.add( Arrays.class );
        final Iterator<Node> iterator = satisfiedBy.iterator();
        for ( int i = 0; i < count; i++ )
        {
          if ( 0 != i )
          {
            code.append( ", " );
          }
          emitNodeAccessor( service, iterator.next(), isOutput, code, args );
        }
        code.append( " ).stream().filter( $T::nonNull ).toList()" );
        args.add( Objects.class );
      }
      else if ( 1 == count )
      {
        code.append( "$T.singletonList( " );
        args.add( Collections.class );
        emitNodeAccessor( service, satisfiedBy.iterator().next(), isOutput, code, args );
        code.append( " )" );
      }
      else
      {
        code.append( "$T.asList( " );
        args.add( Arrays.class );
        final Iterator<Node> iterator = satisfiedBy.iterator();
        for ( int i = 0; i < count; i++ )
        {
          if ( 0 != i )
          {
            code.append( ", " );
          }
          emitNodeAccessor( service, iterator.next(), isOutput, code, args );
        }
        code.append( " )" );
      }
    }
  }

  /**
   * Emit the code required to access the specified node.
   *
   * @param node the node.
   * @param code the code template to append to.
   * @param args the args that passed to javapoet template.
   */
  private static void emitNodeAccessor( @Nonnull final ServiceRequest service,
                                        @Nonnull final Node node,
                                        final boolean isOutput,
                                        @Nonnull final StringBuilder code,
                                        @Nonnull final List<Object> args )
  {
    final ServiceRequest.Kind kind = service.getKind();
    if ( ServiceRequest.Kind.OPTIONAL == kind )
    {
      code.append( "$T.ofNullable( " );
      args.add( Optional.class );
      emitDirectNodeAccess( service, node, isOutput, code, args );
      code.append( " )" );
    }
    else if ( ServiceRequest.Kind.SUPPLIER_OPTIONAL == kind ||
              ServiceRequest.Kind.SUPPLIER_OPTIONAL_COLLECTION == kind )
    {
      code.append( "() -> $T.ofNullable( " );
      args.add( Optional.class );
      emitDirectNodeAccess( service, node, isOutput, code, args );
      code.append( " )" );
    }
    else
    {
      if ( kind.isSupplier() )
      {
        code.append( "() -> " );
      }
      emitDirectNodeAccess( service, node, isOutput, code, args );
    }
  }

  private static void emitAbsentServiceValue( @Nonnull final ServiceRequest service,
                                              @Nonnull final StringBuilder code,
                                              @Nonnull final List<Object> args )
  {
    final ServiceRequest.Kind kind = service.getKind();
    if ( ServiceRequest.Kind.OPTIONAL == kind )
    {
      code.append( "$T.empty()" );
      args.add( Optional.class );
    }
    else if ( ServiceRequest.Kind.SUPPLIER_OPTIONAL == kind )
    {
      code.append( "() -> $T.empty()" );
      args.add( Optional.class );
    }
    else
    {
      code.append( "null" );
    }
  }

  private static void emitDirectNodeAccess( @Nonnull final ServiceRequest service,
                                            @Nonnull final Node node,
                                            final boolean isOutput,
                                            @Nonnull final StringBuilder code,
                                            @Nonnull final List<Object> args )
  {
    if ( ( isOutput && !service.getService().isPublic() ) ||
         ( !node.isPublic() && service.getService().isPublic() ) )
    {
      code.append( "($T) " );
      args.add( service.getService().getCoordinate().getType() );
    }
    code.append( node.isEager() ? "$N" : "$N()" );
    args.add( node.getName() );
  }

  private static void emitFragmentFields( @Nonnull final ProcessingEnvironment processingEnv,
                                          @Nonnull final TypeSpec.Builder builder,
                                          @Nonnull final ComponentGraph graph )
  {
    for ( final FragmentNode node : graph.getFragments() )
    {
      final TypeName type = StingGeneratorUtil.getGeneratedClassName( node.getFragment().getElement() );
      final FieldSpec.Builder field = FieldSpec
        .builder( type, node.getName(), Modifier.PRIVATE, Modifier.FINAL )
        .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
        .initializer( "new $T()", type );
      final List<TypeMirror> types = Collections.singletonList( node.getFragment().getElement().asType() );
      SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv, field, Collections.emptyList(), types );
      builder.addField( field.build() );
    }
  }
}
