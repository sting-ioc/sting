package sting.processor;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.realityforge.proton.ElementsUtil;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.SuppressWarningsUtil;

final class FragmentGenerator
{
  private FragmentGenerator()
  {
  }

  @Nonnull
  static TypeSpec buildType( @Nonnull final ProcessingEnvironment processingEnv,
                             @Nonnull final FragmentDescriptor fragment )
  {
    final TypeElement element = fragment.getElement();
    final TypeSpec.Builder builder =
      TypeSpec
        .classBuilder( StingGeneratorUtil.getGeneratedClassName( element ) )
        .addModifiers( Modifier.PUBLIC, Modifier.FINAL );
    GeneratorUtil.addOriginatingTypes( element, builder );
    GeneratorUtil.copyWhitelistedAnnotations( element, builder );
    final TypeMirror type = element.asType();
    builder.addSuperinterface( TypeName.get( type ) );

    GeneratorUtil.addGeneratedAnnotation( processingEnv, builder, StingProcessor.class.getName() );
    SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv,
                                                        builder,
                                                        Collections.emptyList(),
                                                        Collections.singletonList( type ) );

    for ( final Binding binding : fragment.getBindings() )
    {
      builder.addMethod( buildProvidesStub( processingEnv, binding ) );
    }

    return builder.build();
  }

  @Nonnull
  private static MethodSpec buildProvidesStub( @Nonnull final ProcessingEnvironment processingEnv,
                                               @Nonnull final Binding binding )
  {
    final ExecutableElement element = binding.getElement();
    final TypeMirror returnType = element.getReturnType();
    final boolean isPublic =
      TypeKind.DECLARED != returnType.getKind() ||
      ElementsUtil.isEffectivelyPublic( (TypeElement) ( (DeclaredType) returnType ).asElement() );

    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( StingGeneratorUtil.getFragmentProvidesStubName( element ) )
        .addModifiers( Modifier.PUBLIC )
        .returns( isPublic ? TypeName.get( returnType ) : TypeName.OBJECT );
    GeneratorUtil.copyWhitelistedAnnotations( element, method );

    final StringBuilder code = new StringBuilder();
    final List<Object> args = new ArrayList<>();
    code.append( "return $N" );
    args.add( element.getSimpleName().toString() );

    return StingGeneratorUtil.buildBindingCreator( processingEnv, method, code, args, returnType, binding );
  }
}
