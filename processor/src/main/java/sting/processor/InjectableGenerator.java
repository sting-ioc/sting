package sting.processor;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
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
import org.realityforge.proton.SuppressWarningsUtil;

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
    args.add( element.asType() );
    if ( !originalParameters.isEmpty() )
    {
      code.append( ' ' );
    }
    boolean allPublic = true;
    boolean firstParam = true;
    final ArrayList<TypeMirror> typesProcessed = new ArrayList<>();
    typesProcessed.add( element.asType() );
    final DependencyDescriptor[] dependencies = injectable.getBinding().getDependencies();
    for ( int i = 0; i < dependencies.length; i++ )
    {
      final VariableElement parameter = originalParameters.get( i );
      final String paramName = parameter.getSimpleName().toString();
      final DependencyDescriptor dependency = dependencies[ i ];

      final TypeMirror valueType = dependency.getCoordinate().getType();
      typesProcessed.add( valueType );
      final boolean isPublic =
        TypeKind.DECLARED != valueType.getKind() ||
        StingElementsUtil.isEffectivelyPublic( (TypeElement) ( (DeclaredType) valueType ).asElement() );
      allPublic &= isPublic;

      final DependencyDescriptor.Type type = dependency.getType();
      final TypeName actualTypeName;
      if ( DependencyDescriptor.Type.INSTANCE == type )
      {
        actualTypeName = TypeName.get( valueType );
      }
      else if ( DependencyDescriptor.Type.SUPPLIER == type )
      {
        actualTypeName = ParameterizedTypeName.get( StingTypeNames.SUPPLIER, TypeName.get( valueType ) );
      }
      else if ( DependencyDescriptor.Type.COLLECTION == type )
      {
        actualTypeName = ParameterizedTypeName.get( StingTypeNames.COLLECTION, TypeName.get( valueType ) );
      }
      else
      {
        assert DependencyDescriptor.Type.SUPPLIER_COLLECTION == type;
        actualTypeName = ParameterizedTypeName.get( StingTypeNames.COLLECTION,
                                                    ParameterizedTypeName.get( StingTypeNames.SUPPLIER,
                                                                               TypeName.get( valueType ) ) );
      }

      final TypeName paramType = isPublic ? actualTypeName : TypeName.OBJECT;
      final ParameterSpec.Builder param = ParameterSpec.builder( paramType, paramName, Modifier.FINAL );
      GeneratorUtil.copyWhitelistedAnnotations( parameter, param );
      creator.addParameter( param.build() );
      if ( !firstParam )
      {
        code.append( ", " );
      }
      firstParam = false;
      if ( !dependency.isOptional() )
      {
        code.append( "$T.requireNonNull( " );
        args.add( Objects.class );
      }
      if ( !isPublic )
      {
        code.append( "($T) " );
        args.add( actualTypeName );
      }
      code.append( "$N" );
      args.add( paramName );
      if ( !dependency.isOptional() )
      {
        code.append( " )" );
      }
    }
    if ( !originalParameters.isEmpty() )
    {
      code.append( ' ' );
    }
    code.append( ")" );
    creator.addStatement( code.toString(), args.toArray() );
    if ( !allPublic )
    {
      SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv,
                                                          creator,
                                                          Collections.singleton( "unchecked" ),
                                                          typesProcessed );
    }
    builder.addMethod( creator.build() );
    return builder.build();
  }
}
