package sting.processor;

import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.SuppressWarningsUtil;

final class FactoryGenerator
{
  private FactoryGenerator()
  {
  }

  @Nonnull
  static TypeSpec buildType( @Nonnull final ProcessingEnvironment processingEnv,
                             @Nonnull final FactoryDescriptor factory )
  {
    final TypeElement element = factory.getElement();
    final TypeMirror type = element.asType();
    final TypeSpec.Builder builder =
      TypeSpec
        .classBuilder( StingGeneratorUtil.getGeneratedClassName( element ) )
        .addModifiers( Modifier.PUBLIC, Modifier.FINAL )
        .addAnnotation( ClassName.get( "sting", "Injectable" ) )
        .addAnnotation( AnnotationSpec.builder( ClassName.get( "sting", "Typed" ) )
                          .addMember( "value", "$T.class", type )
                          .build() )
        .addSuperinterface( TypeName.get( type ) );
    GeneratorUtil.addOriginatingTypes( element, builder );
    GeneratorUtil.copyWhitelistedAnnotations( element, builder );
    GeneratorUtil.addGeneratedAnnotation( processingEnv, builder, StingProcessor.class.getName() );
    SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv,
                                                        builder,
                                                        Collections.emptyList(),
                                                        Collections.singletonList( type ) );

    emitFields( processingEnv, factory, builder );
    emitConstructor( factory, builder );
    emitMethods( processingEnv, factory, builder );

    return builder.build();
  }

  private static void emitFields( @Nonnull final ProcessingEnvironment processingEnv,
                                  @Nonnull final FactoryDescriptor factory,
                                  @Nonnull final TypeSpec.Builder builder )
  {
    for ( final FactoryDependencyDescriptor dependency : factory.getDependencies() )
    {
      final ServiceRequest serviceRequest = dependency.getServiceRequest();
      final ServiceSpec service = serviceRequest.getService();
      final TypeName type = StingGeneratorUtil.getServiceType( serviceRequest );
      final FieldSpec.Builder field =
        FieldSpec
          .builder( type, dependency.getFieldName(), Modifier.PRIVATE, Modifier.FINAL );
      if ( !type.isPrimitive() )
      {
        field.addAnnotation( service.isOptional() ?
                             GeneratorUtil.NULLABLE_CLASSNAME :
                             GeneratorUtil.NONNULL_CLASSNAME );
      }
      final List<TypeMirror> types = Collections.singletonList( service.getCoordinate().getType() );
      SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv, field, Collections.emptyList(), types );
      builder.addField( field.build() );
    }
  }

  private static void emitConstructor( @Nonnull final FactoryDescriptor factory,
                                       @Nonnull final TypeSpec.Builder builder )
  {
    final MethodSpec.Builder ctor = MethodSpec.constructorBuilder();
    for ( final FactoryDependencyDescriptor dependency : factory.getDependencies() )
    {
      final ServiceRequest serviceRequest = dependency.getServiceRequest();
      final VariableElement parameterElement = (VariableElement) serviceRequest.getElement();
      final ParameterSpec.Builder parameter =
        ParameterSpec.builder( StingGeneratorUtil.getServiceType( serviceRequest ),
                               dependency.getParameterName(),
                               Modifier.FINAL );
      GeneratorUtil.copyWhitelistedAnnotations( parameterElement, parameter );
      ctor.addParameter( parameter.build() );
      ctor.addStatement( "$N = $N", dependency.getFieldName(), dependency.getParameterName() );
    }
    builder.addMethod( ctor.build() );
  }

  private static void emitMethods( @Nonnull final ProcessingEnvironment processingEnv,
                                   @Nonnull final FactoryDescriptor factory,
                                   @Nonnull final TypeSpec.Builder builder )
  {
    for ( final FactoryMethodDescriptor methodDescriptor : factory.getMethods() )
    {
      final MethodSpec.Builder method =
        MethodSpec
          .methodBuilder( methodDescriptor.getMethod().getSimpleName().toString() )
          .addAnnotation( Override.class )
          .addModifiers( Modifier.PUBLIC )
          .returns( TypeName.get( methodDescriptor.getMethod().getReturnType() ) );
      GeneratorUtil.copyWhitelistedAnnotations( methodDescriptor.getMethod(), method );
      for ( final VariableElement parameterElement : methodDescriptor.getMethod().getParameters() )
      {
        final ParameterSpec.Builder parameter =
          ParameterSpec.builder( TypeName.get( parameterElement.asType() ), parameterElement.getSimpleName().toString() );
        GeneratorUtil.copyWhitelistedAnnotations( parameterElement, parameter );
        method.addParameter( parameter.build() );
      }
      SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv,
                                                          method,
                                                          Collections.emptyList(),
                                                          Collections.singletonList( methodDescriptor
                                                                                       .getMethod()
                                                                                       .asType() ) );

      final StringBuilder code = new StringBuilder();
      final List<Object> args = new ArrayList<>();
      code.append( "return new $T(" );
      args.add( methodDescriptor.getProducedType() );
      final List<? extends VariableElement> constructorParameters = methodDescriptor.getConstructorParameters();
      final Map<Integer, VariableElement> methodParametersByIndex =
        methodDescriptor.getMethodParametersByConstructorIndex();
      final Map<Integer, FactoryDependencyDescriptor> dependenciesByIndex =
        methodDescriptor.getDependenciesByConstructorIndex();
      for ( int i = 0; i < constructorParameters.size(); i++ )
      {
        if ( 0 != i )
        {
          code.append( ", " );
        }
        final VariableElement factoryParameter = methodParametersByIndex.get( i );
        if ( null != factoryParameter )
        {
          code.append( "$N" );
          args.add( factoryParameter.getSimpleName().toString() );
        }
        else
        {
          final FactoryDependencyDescriptor dependency = dependenciesByIndex.get( i );
          assert null != dependency;
          code.append( "$N" );
          args.add( dependency.getFieldName() );
        }
      }
      code.append( ")" );
      method.addStatement( code.toString(), args.toArray() );
      builder.addMethod( method.build() );
    }
  }
}
