package sting.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.realityforge.proton.ElementsUtil;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.SuppressWarningsUtil;

final class StingGeneratorUtil
{
  @Nonnull
  private static final String FRAMEWORK_PREFIX = "$sting$_";
  @Nonnull
  private static final ClassName COLLECTION = ClassName.get( Collection.class );
  @Nonnull
  private static final ClassName SUPPLIER = ClassName.get( Supplier.class );

  private StingGeneratorUtil()
  {
  }

  static TypeName getServiceType( @Nonnull final ServiceDescriptor service )
  {
    final ServiceDescriptor.Kind kind = service.getKind();
    final TypeName baseType = TypeName.get( service.getCoordinate().getType() );
    if ( ServiceDescriptor.Kind.INSTANCE == kind )
    {
      return baseType;
    }
    else if ( ServiceDescriptor.Kind.SUPPLIER == kind )
    {
      return ParameterizedTypeName.get( SUPPLIER, baseType );
    }
    else if ( ServiceDescriptor.Kind.COLLECTION == kind )
    {
      return ParameterizedTypeName.get( COLLECTION, baseType );
    }
    else
    {
      assert ServiceDescriptor.Kind.SUPPLIER_COLLECTION == kind;
      return ParameterizedTypeName.get( COLLECTION,
                                        ParameterizedTypeName.get( SUPPLIER, baseType ) );
    }
  }

  @Nonnull
  static MethodSpec buildBindingCreator( @Nonnull final ProcessingEnvironment processingEnv,
                                         @Nonnull final MethodSpec.Builder method,
                                         @Nonnull final StringBuilder code,
                                         @Nonnull final List<Object> args,
                                         @Nonnull final TypeMirror typeProduced,
                                         @Nonnull final Binding binding )
  {
    final ServiceDescriptor[] dependencies = binding.getDependencies();

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
    for ( final ServiceDescriptor service : dependencies )
    {
      final VariableElement parameter = (VariableElement) service.getElement();
      final String paramName = parameter.getSimpleName().toString();

      typesProcessed.add( service.getCoordinate().getType() );
      final boolean isPublic = service.isPublic();
      allPublic &= isPublic;

      final TypeName actualTypeName = getServiceType( service );

      final ServiceDescriptor.Kind kind = service.getKind();
      final TypeName paramType;
      if ( isPublic )
      {
        paramType = actualTypeName;
      }
      else if ( ServiceDescriptor.Kind.INSTANCE == kind )
      {
        paramType = TypeName.OBJECT;
      }
      else if ( ServiceDescriptor.Kind.SUPPLIER == kind )
      {
        anyNonPublicNonInstance = true;
        paramType = SUPPLIER;
      }
      else if ( ServiceDescriptor.Kind.COLLECTION == kind )
      {
        anyNonPublicNonInstance = true;
        paramType = COLLECTION;
      }
      else
      {
        assert ServiceDescriptor.Kind.SUPPLIER_COLLECTION == kind;
        anyNonPublicNonInstance = true;
        paramType = COLLECTION;
      }
      final ParameterSpec.Builder param = ParameterSpec.builder( paramType, paramName, Modifier.FINAL );
      GeneratorUtil.copyWhitelistedAnnotations( parameter, param );
      method.addParameter( param.build() );
      if ( !firstParam )
      {
        code.append( ", " );
      }
      firstParam = false;
      final boolean requireNonNull = !service.isOptional() && !typeProduced.getKind().isPrimitive();
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
    if ( ElementsUtil.isElementDeprecated( binding.getElement() ) )
    {
      additionalSuppressions.add( "deprecation" );
    }
    SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv, method, additionalSuppressions, typesProcessed );
    return method.build();
  }

  @Nonnull
  static ClassName getGeneratedClassName( @Nonnull final TypeElement element )
  {
    return GeneratorUtil.getGeneratedClassName( element, "Sting_", "" );
  }

  @Nonnull
  static String getFragmentProvidesStubName( @Nonnull final ExecutableElement element )
  {
    return FRAMEWORK_PREFIX + element.getSimpleName().toString();
  }
}
