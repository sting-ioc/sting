package sting.processor;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.SuppressWarningsUtil;

final class StingGeneratorUtil
{
  @Nonnull
  static final String FRAMEWORK_PREFIX = "$sting$_";

  private StingGeneratorUtil()
  {
  }

  static TypeName getDependencyType( @Nonnull final DependencyDescriptor dependency )
  {
    final TypeMirror valueType = dependency.getCoordinate().getType();
    final DependencyDescriptor.Type type = dependency.getType();
    final TypeName baseType = TypeName.get( valueType );
    if ( DependencyDescriptor.Type.INSTANCE == type )
    {
      return baseType;
    }
    else if ( DependencyDescriptor.Type.SUPPLIER == type )
    {
      return ParameterizedTypeName.get( StingTypeNames.SUPPLIER, baseType );
    }
    else if ( DependencyDescriptor.Type.COLLECTION == type )
    {
      return ParameterizedTypeName.get( StingTypeNames.COLLECTION, baseType );
    }
    else
    {
      assert DependencyDescriptor.Type.SUPPLIER_COLLECTION == type;
      return ParameterizedTypeName.get( StingTypeNames.COLLECTION,
                                        ParameterizedTypeName.get( StingTypeNames.SUPPLIER, baseType ) );
    }
  }

  @Nonnull
  static MethodSpec buildBindingCreator( @Nonnull final ProcessingEnvironment processingEnv,
                                         @Nonnull final MethodSpec.Builder method,
                                         @Nonnull final StringBuilder code,
                                         @Nonnull final List<Object> args,
                                         @Nonnull final TypeMirror typeProduced,
                                         @Nonnull final DependencyDescriptor[] dependencies )
  {
    final List<TypeMirror> typesProcessed = new ArrayList<>();
    typesProcessed.add( typeProduced );

    code.append( "(" );
    if ( 0 != dependencies.length )
    {
      code.append( ' ' );
    }
    boolean allPublic = true;
    boolean firstParam = true;
    for ( final DependencyDescriptor dependency : dependencies )
    {
      final VariableElement parameter = (VariableElement) dependency.getElement();
      final String paramName = parameter.getSimpleName().toString();

      final TypeMirror valueType = dependency.getCoordinate().getType();
      typesProcessed.add( valueType );
      final boolean isPublic =
        TypeKind.DECLARED != valueType.getKind() ||
        StingElementsUtil.isEffectivelyPublic( (TypeElement) ( (DeclaredType) valueType ).asElement() );
      allPublic &= isPublic;

      final TypeName actualTypeName = getDependencyType( dependency );

      final TypeName paramType = isPublic ? actualTypeName : TypeName.OBJECT;
      final ParameterSpec.Builder param = ParameterSpec.builder( paramType, paramName, Modifier.FINAL );
      GeneratorUtil.copyWhitelistedAnnotations( parameter, param );
      method.addParameter( param.build() );
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
    if ( 0 != dependencies.length )
    {
      code.append( ' ' );
    }
    code.append( ")" );
    method.addStatement( code.toString(), args.toArray() );
    if ( !allPublic )
    {
      SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv,
                                                          method,
                                                          Collections.singleton( "unchecked" ),
                                                          typesProcessed );
    }
    return method.build();
  }
}
