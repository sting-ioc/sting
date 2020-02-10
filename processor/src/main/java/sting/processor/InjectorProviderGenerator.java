package sting.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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


    emitProvide( graph, builder );

    return builder.build();
  }

  private static void emitProvide( @Nonnull final ComponentGraph graph, @Nonnull final TypeSpec.Builder builder )
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
}
