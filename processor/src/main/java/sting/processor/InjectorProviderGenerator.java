package sting.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import org.realityforge.proton.ElementsUtil;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.SuppressWarningsUtil;

final class InjectorProviderGenerator
{
  private InjectorProviderGenerator()
  {
  }

  @Nonnull
  static TypeSpec buildType( @Nonnull final ProcessingEnvironment processingEnv, @Nonnull final ComponentGraph graph )
  {
    final InjectorDescriptor injector = graph.getInjector();
    final TypeElement element = injector.getElement();
    final TypeSpec.Builder builder =
      TypeSpec
        .interfaceBuilder( GeneratorUtil.getGeneratedSimpleClassName( element, "Sting_", "_Provider" ) )
        .addModifiers( Modifier.PUBLIC );
    GeneratorUtil.addOriginatingTypes( element, builder );
    GeneratorUtil.addGeneratedAnnotation( processingEnv, builder, StingProcessor.class.getName() );

    GeneratorUtil.copyWhitelistedAnnotations( element, builder );
    SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv,
                                                        builder,
                                                        Collections.emptyList(),
                                                        Collections.singletonList( element.asType() ) );

    builder.addAnnotation( ClassName.get( "sting", "Fragment" ) );

    emitInjectorProvide( graph, builder );
    emitOutputProvides( processingEnv, graph, builder );

    return builder.build();
  }

  private static void emitInjectorProvide( @Nonnull final ComponentGraph graph,
                                           @Nonnull final TypeSpec.Builder builder )
  {
    final InjectorDescriptor injector = graph.getInjector();
    final TypeElement element = injector.getElement();

    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( "provide" )
        .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
        .addModifiers( Modifier.PUBLIC, Modifier.DEFAULT )
        .returns( TypeName.get( element.asType() ) );

    final List<InputDescriptor> inputs = graph.getInjector().getInputs();
    for ( final InputDescriptor input : inputs )
    {
      final ServiceSpec service = input.getService();
      final Coordinate coordinate = service.getCoordinate();
      final ParameterSpec.Builder parameter =
        ParameterSpec
          .builder( TypeName.get( coordinate.getType() ),
                    input.getName(),
                    Modifier.FINAL );
      if ( !coordinate.getType().getKind().isPrimitive() )
      {
        parameter.addAnnotation( service.isOptional() ?
                                 GeneratorUtil.NULLABLE_CLASSNAME :
                                 GeneratorUtil.NONNULL_CLASSNAME );
      }
      method.addParameter( parameter.build() );
    }
    method.addStatement( "return new $T(" +
                         inputs.stream().map( InputDescriptor::getName ).collect( Collectors.joining( ", " ) ) +
                         ")",
                         StingGeneratorUtil.getGeneratedClassName( element ) );

    builder.addMethod( method.build() );
  }

  private static void emitOutputProvides( @Nonnull final ProcessingEnvironment processingEnv,
                                          @Nonnull final ComponentGraph graph,
                                          @Nonnull final TypeSpec.Builder builder )
  {
    final InjectorDescriptor injector = graph.getInjector();
    final TypeElement element = injector.getElement();

    for ( final Edge edge : graph.getRootNode().getDependsOn() )
    {
      final ServiceDescriptor service = edge.getService();
      if ( ServiceDescriptor.Kind.INSTANCE == service.getKind() )
      {
        final MethodSpec.Builder method =
          MethodSpec
            .methodBuilder( service.getElement().getSimpleName().toString() )
            .addModifiers( Modifier.PUBLIC, Modifier.DEFAULT )
            .returns( TypeName.get( service.getService().getCoordinate().getType() ) )
            .addParameter( ParameterSpec
                             .builder( TypeName.get( element.asType() ), "injector", Modifier.FINAL )
                             .build() );

        GeneratorUtil.copyWhitelistedAnnotations( service.getElement(), method );
        final ArrayList<String> additionalSuppressions = new ArrayList<>();
        if ( ElementsUtil.isElementDeprecated( service.getElement() ) )
        {
          additionalSuppressions.add( "deprecation" );
        }
        final List<TypeMirror> types =
          Arrays.asList( element.asType(), service.getService().getCoordinate().getType() );
        SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv, method, additionalSuppressions, types );
        method.addStatement( "return injector.$N()", service.getElement().getSimpleName().toString() );
        builder.addMethod( method.build() );
      }
    }
  }
}
