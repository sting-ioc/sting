package sting.processor;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import org.realityforge.proton.GeneratorUtil;

final class InjectorGenerator
{
  private InjectorGenerator()
  {
  }

  @Nonnull
  static TypeSpec buildType( @Nonnull final ProcessingEnvironment processingEnv, @Nonnull final ObjectGraph graph )
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

    emitFragmentFields( builder, graph );
    emitNodeFields( graph, builder );
    emitConstructor( graph, builder );
    emitNodeAccessorMethod( graph, builder );
    emitTopLevelDependencyMethods( processingEnv, graph, builder );

    return builder.build();
  }

  private static void emitTopLevelDependencyMethods( @Nonnull final ProcessingEnvironment processingEnv,
                                                     @Nonnull final ObjectGraph graph,
                                                     @Nonnull final TypeSpec.Builder builder )
  {
    for ( final Edge edge : graph.getRootNode().getDependsOn() )
    {
      final DependencyDescriptor dependency = edge.getDependency();
      final MethodSpec.Builder method =
        MethodSpec.overriding( (ExecutableElement) dependency.getElement(),
                               (DeclaredType) graph.getInjector().getElement().asType(),
                               processingEnv.getTypeUtils() );
      GeneratorUtil.copyWhitelistedAnnotations( dependency.getElement(), method );

      final StringBuilder code = new StringBuilder();
      final ArrayList<Object> args = new ArrayList<>();
      code.append( "return " );
      emitDependencyValue( edge, code, args );
      method.addStatement( code.toString(), args.toArray() );
      builder.addMethod( method.build() );
    }
  }

  private static void emitConstructor( @Nonnull final ObjectGraph graph, @Nonnull final TypeSpec.Builder builder )
  {
    final MethodSpec.Builder ctor = MethodSpec.constructorBuilder();

    for ( final Node node : graph.getNodes() )
    {
      if ( node.isEager() )
      {
        final StringBuilder code = new StringBuilder();
        final List<Object> args = new ArrayList<>();
        provideAndAssign( node, code, args );
        ctor.addStatement( code.toString(), args.toArray() );
      }
    }

    builder.addMethod( ctor.build() );
  }

  private static void emitNodeFields( @Nonnull final ObjectGraph graph,
                                      @Nonnull final TypeSpec.Builder builder )
  {
    for ( final Node node : graph.getNodes() )
    {
      final boolean isNonnull = node.isEager() && Binding.Type.NULLABLE_PROVIDES != node.getBinding().getBindingType();
      final FieldSpec.Builder field =
        FieldSpec
          .builder( getPublicTypeName( node ), node.getName(), Modifier.PRIVATE )
          .addAnnotation( isNonnull ? GeneratorUtil.NONNULL_CLASSNAME : GeneratorUtil.NULLABLE_CLASSNAME );
      if ( node.isEager() )
      {
        field.addModifiers( Modifier.FINAL );
      }

      builder.addField( field.build() );

      if ( !node.isEager() &&
           ( Binding.Type.NULLABLE_PROVIDES == node.getBinding().getBindingType() ||
             node.getType().getKind().isPrimitive() ) )
      {
        builder.addField( FieldSpec.builder( TypeName.BOOLEAN, getFlagFieldName( node ), Modifier.PRIVATE ).build() );
      }
    }
  }

  private static TypeName getPublicTypeName( @Nonnull final Node node )
  {
    if ( node.isPublicAccess() )
    {
      return TypeName.get( node.getType() );
    }
    else
    {
      return TypeName.OBJECT;
    }
  }

  private static void emitNodeAccessorMethod( @Nonnull final ObjectGraph graph,
                                              @Nonnull final TypeSpec.Builder builder )
  {
    for ( final Node node : graph.getNodes() )
    {
      if ( !node.isEager() )
      {
        final MethodSpec.Builder method =
          MethodSpec
            .methodBuilder( node.getName() )
            .addModifiers( Modifier.PRIVATE )
            .returns( getPublicTypeName( node ) );
        final boolean isNonnull = Binding.Type.NULLABLE_PROVIDES != node.getBinding().getBindingType();
        final boolean isNonPrimitive = !node.getType().getKind().isPrimitive();
        if ( isNonPrimitive )
        {
          method.addAnnotation( isNonnull ? GeneratorUtil.NONNULL_CLASSNAME : GeneratorUtil.NULLABLE_CLASSNAME );
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
    final boolean isNonnull = Binding.Type.NULLABLE_PROVIDES != node.getBinding().getBindingType();
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
      args.add( StingGeneratorUtil.FRAMEWORK_PREFIX + node.getBinding().getElement().getSimpleName().toString() );
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
      emitDependencyValue( edge, code, args );
    }
    code.append( ')' );
    if ( requireNonNull )
    {
      code.append( " )" );
    }
  }

  private static void emitDependencyValue( @Nonnull final Edge edge,
                                           @Nonnull final StringBuilder code,
                                           @Nonnull final List<Object> args )
  {
    final Collection<Node> satisfiedBy = edge.getSatisfiedBy();
    final DependencyDescriptor dependency = edge.getDependency();
    final DependencyDescriptor.Type depType = dependency.getType();
    if ( !depType.isCollection() )
    {
      if ( satisfiedBy.isEmpty() )
      {
        code.append( "null" );
      }
      else
      {
        emitNodeAccessor( dependency, satisfiedBy.iterator().next(), code, args );
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
      else if ( 1 == count )
      {
        code.append( "$T.singletonList( " );
        args.add( Collections.class );
        emitNodeAccessor( dependency, satisfiedBy.iterator().next(), code, args );
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
          emitNodeAccessor( dependency, iterator.next(), code, args );
        }
        code.append( " )" );
      }
    }
  }

  /**
   * Emit the code required to access the specified node.
   *
   * @param node the node value.
   * @param code the code template to append to.
   * @param args the args that passed to javapoet template.
   */
  private static void emitNodeAccessor( @Nonnull final DependencyDescriptor dependency,
                                        @Nonnull final Node node,
                                        @Nonnull final StringBuilder code,
                                        @Nonnull final List<Object> args )
  {
    final DependencyDescriptor.Type depType = dependency.getType();
    if ( depType.isSupplier() )
    {
      code.append( "() -> " );
    }
    if ( !dependency.isPublic() )
    {
      code.append( "($T) " );
      args.add( dependency.getCoordinate().getType() );
    }
    if ( node.isEager() )
    {
      code.append( "$N" );
      args.add( node.getName() );
    }
    else
    {
      code.append( "$N()" );
      args.add( node.getName() );
    }
  }

  private static void emitFragmentFields( @Nonnull final TypeSpec.Builder builder, @Nonnull final ObjectGraph graph )
  {
    for ( final FragmentNode node : graph.getFragments() )
    {
      final TypeName type = StingGeneratorUtil.getGeneratedClassName( node.getFragment().getElement() );
      builder.addField( FieldSpec
                          .builder( type, node.getName(), Modifier.PRIVATE, Modifier.FINAL )
                          .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                          .initializer( "new $T()", type )
                          .build() );

    }
  }
}
