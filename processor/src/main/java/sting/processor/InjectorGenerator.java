package sting.processor;

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
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
        .classBuilder( GeneratorUtil.getGeneratedClassName( element, "Sting_", "" ) )
        .addModifiers( Modifier.FINAL );
    GeneratorUtil.addOriginatingTypes( element, builder );
    GeneratorUtil.copyWhitelistedAnnotations( element, builder );

    if ( false )
    {
      if ( injector.isClassType() )
      {
        builder.superclass( TypeName.get( element.asType() ) );
      }
      else
      {
        builder.addSuperinterface( TypeName.get( element.asType() ) );
      }
    }

    GeneratorUtil.addGeneratedAnnotation( processingEnv, builder, StingProcessor.class.getName() );

    return builder.build();
  }
}
