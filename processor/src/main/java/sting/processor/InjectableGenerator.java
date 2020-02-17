package sting.processor;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.realityforge.proton.ElementsUtil;
import org.realityforge.proton.GeneratorUtil;

final class InjectableGenerator
{
  private InjectableGenerator()
  {
  }

  @Nonnull
  static TypeSpec buildType( @Nonnull final ProcessingEnvironment processingEnv,
                             @Nonnull final InjectableDescriptor injectable )
  {
    final TypeElement element = injectable.getElement();
    final TypeSpec.Builder builder =
      TypeSpec
        .classBuilder( StingGeneratorUtil.getGeneratedClassName( element ) )
        .addModifiers( Modifier.PUBLIC, Modifier.FINAL );
    GeneratorUtil.addOriginatingTypes( element, builder );
    GeneratorUtil.addGeneratedAnnotation( processingEnv, builder, StingProcessor.class.getName() );

    builder.addMethod( MethodSpec.constructorBuilder().addModifiers( Modifier.PRIVATE ).build() );

    final TypeMirror returnType = element.asType();

    final boolean isPublic =
      TypeKind.DECLARED != returnType.getKind() ||
      ElementsUtil.isEffectivelyPublic( (TypeElement) ( (DeclaredType) returnType ).asElement() );

    final MethodSpec.Builder creator =
      MethodSpec
        .methodBuilder( "create" )
        .addModifiers( Modifier.PUBLIC, Modifier.STATIC )
        .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
        .returns( isPublic ? TypeName.get( returnType ) : TypeName.OBJECT );
    final StringBuilder code = new StringBuilder();
    final List<Object> args = new ArrayList<>();
    code.append( "return new $T" );
    args.add( returnType );

    builder.addMethod( StingGeneratorUtil.buildBindingCreator( processingEnv,
                                                               creator,
                                                               code,
                                                               args,
                                                               returnType,
                                                               injectable.getBinding() ) );

    return builder.build();
  }
}
