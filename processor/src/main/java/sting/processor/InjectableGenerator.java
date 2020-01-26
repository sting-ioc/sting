package sting.processor;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
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
        .classBuilder( GeneratorUtil.getGeneratedClassName( element, "Sting_", "" ) )
        .addModifiers( Modifier.PUBLIC, Modifier.FINAL );
    GeneratorUtil.addOriginatingTypes( element, builder );
    GeneratorUtil.addGeneratedAnnotation( processingEnv, builder, StingProcessor.class.getName() );

    builder.addMethod( MethodSpec.constructorBuilder().addModifiers( Modifier.PRIVATE ).build() );

    final TypeName returnType = TypeName.get( element.asType() );
    final MethodSpec.Builder creator =
      MethodSpec
        .methodBuilder( "create" )
        .addModifiers( Modifier.PUBLIC, Modifier.STATIC )
        .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
        .returns( returnType );
    final ExecutableElement original = ElementsUtil.getConstructors( element ).get( 0 );
    final List<? extends VariableElement> originalParameters = original.getParameters();
    final StringBuilder code = new StringBuilder();
    final List<Object> args = new ArrayList<>();
    code.append( "return new $T(" );
    args.add(  element.asType() );
    if ( !originalParameters.isEmpty() )
    {
      code.append( ' ' );
    }
    boolean firstParam = true;
    for ( final VariableElement parameter : originalParameters )
    {
      final TypeName paramType = TypeName.get( parameter.asType() );
      final String paramName = parameter.getSimpleName().toString();
      final ParameterSpec.Builder param = ParameterSpec.builder( paramType, paramName, Modifier.FINAL );
      GeneratorUtil.copyWhitelistedAnnotations( parameter, param );
      creator.addParameter( param.build() );
      if ( !firstParam )
      {
        code.append( ", " );
      }
      firstParam = false;
      code.append( "$N" );
      args.add( paramName );
    }
    if ( !originalParameters.isEmpty() )
    {
      code.append( ' ' );
    }
    code.append( ")" );
    creator.addStatement( code.toString(), args.toArray() );
    builder.addMethod( creator.build() );
    return builder.build();
  }
}
