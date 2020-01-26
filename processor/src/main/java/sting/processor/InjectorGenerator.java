package sting.processor;

import com.squareup.javapoet.FieldSpec;
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
        .classBuilder( StingGeneratorUtil.getGeneratedClassName( element ) )
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

    for ( final FragmentNode node : graph.getFragments() )
    {
      final TypeName type = StingGeneratorUtil.getGeneratedClassName( node.getFragment().getElement() );
      builder.addField( FieldSpec
                          .builder( type, node.getName(), Modifier.PRIVATE, Modifier.FINAL )
                          .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                          .initializer( "new $T()", type )
                          .build() );

    }

    GeneratorUtil.addGeneratedAnnotation( processingEnv, builder, StingProcessor.class.getName() );

    return builder.build();
  }
}
