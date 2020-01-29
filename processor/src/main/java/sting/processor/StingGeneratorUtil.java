package sting.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
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
    final DependencyDescriptor.Kind kind = dependency.getKind();
    final TypeName baseType = TypeName.get( valueType );
    if ( DependencyDescriptor.Kind.INSTANCE == kind )
    {
      return baseType;
    }
    else if ( DependencyDescriptor.Kind.SUPPLIER == kind )
    {
      return ParameterizedTypeName.get( StingTypeNames.SUPPLIER, baseType );
    }
    else if ( DependencyDescriptor.Kind.COLLECTION == kind )
    {
      return ParameterizedTypeName.get( StingTypeNames.COLLECTION, baseType );
    }
    else
    {
      assert DependencyDescriptor.Kind.SUPPLIER_COLLECTION == kind;
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
    boolean anyNonPublicNonInstance = false;
    boolean firstParam = true;
    for ( final DependencyDescriptor dependency : dependencies )
    {
      final VariableElement parameter = (VariableElement) dependency.getElement();
      final String paramName = parameter.getSimpleName().toString();

      final TypeMirror valueType = dependency.getCoordinate().getType();
      typesProcessed.add( valueType );
      final boolean isPublic = dependency.isPublic();
      allPublic &= isPublic;

      final TypeName actualTypeName = getDependencyType( dependency );

      final DependencyDescriptor.Kind kind = dependency.getKind();
      final TypeName paramType;
      if ( isPublic )
      {
        paramType = actualTypeName;
      }
      else if ( DependencyDescriptor.Kind.INSTANCE == kind )
      {
        paramType = TypeName.OBJECT;
      }
      else if ( DependencyDescriptor.Kind.SUPPLIER == kind )
      {
        anyNonPublicNonInstance = true;
        paramType = StingTypeNames.SUPPLIER;
      }
      else if ( DependencyDescriptor.Kind.COLLECTION == kind )
      {
        anyNonPublicNonInstance = true;
        paramType = StingTypeNames.COLLECTION;
      }
      else
      {
        assert DependencyDescriptor.Kind.SUPPLIER_COLLECTION == kind;
        anyNonPublicNonInstance = true;
        paramType = StingTypeNames.COLLECTION;
      }
      final ParameterSpec.Builder param = ParameterSpec.builder( paramType, paramName, Modifier.FINAL );
      GeneratorUtil.copyWhitelistedAnnotations( parameter, param );
      method.addParameter( param.build() );
      if ( !firstParam )
      {
        code.append( ", " );
      }
      firstParam = false;
      final boolean requireNonNull = !dependency.isOptional() && !typeProduced.getKind().isPrimitive();
      if ( requireNonNull )
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
      if ( requireNonNull )
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
    final ArrayList<String> additionalSuppressions = new ArrayList<>();
    if ( !allPublic )
    {
      additionalSuppressions.add( "unchecked" );
    }
    if ( anyNonPublicNonInstance )
    {
      additionalSuppressions.add( "rawtypes" );
    }
    SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv, method, additionalSuppressions, typesProcessed );
    return method.build();
  }

  @Nonnull
  static ClassName getGeneratedClassName( @Nonnull final TypeElement element )
  {
    return GeneratorUtil.getGeneratedClassName( element, "Sting_", "" );
  }
}
