package sting.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import org.realityforge.proton.AbstractStandardProcessor;
import org.realityforge.proton.AnnotationsUtil;
import org.realityforge.proton.ElementsUtil;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.MemberChecks;
import org.realityforge.proton.ProcessorException;

/**
 * Annotation processor that analyzes sting annotated source and generates dependency injection container.
 */
@SupportedAnnotationTypes( { Constants.INJECTABLE_CLASSNAME, Constants.DEPENDENCY_CLASSNAME } )
@SupportedSourceVersion( SourceVersion.RELEASE_8 )
@SupportedOptions( { "sting.defer.unresolved", "sting.defer.errors" } )
public final class StingProcessor
  extends AbstractStandardProcessor
{
  /**
   * A local cache of bindings that is cleared on error or when processing is complete.
   * This will probably be loaded from json cache files in the future but now we require
   * in memory processing.
   */
  @Nonnull
  private final BindingRegistry _bindingRegistry = new BindingRegistry();

  @Nonnull
  @Override
  protected String getIssueTrackerURL()
  {
    return "https://github.com/realityforge/sting/issues";
  }

  @Nonnull
  @Override
  protected String getOptionPrefix()
  {
    return "sting";
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public boolean process( @Nonnull final Set<? extends TypeElement> annotations, @Nonnull final RoundEnvironment env )
  {
    annotations.stream()
      .filter( a -> a.getQualifiedName().toString().equals( Constants.INJECTABLE_CLASSNAME ) )
      .findAny()
      .ifPresent( a -> processTypeElements( env,
                                            (Collection<TypeElement>) env.getElementsAnnotatedWith( a ),
                                            this::processInjectable ) );

    annotations.stream()
      .filter( a -> a.getQualifiedName().toString().equals( Constants.DEPENDENCY_CLASSNAME ) )
      .findAny()
      .ifPresent( a -> verifyDependencyElements( env, env.getElementsAnnotatedWith( a ) ) );

    errorIfProcessingOverAndInvalidTypesDetected( env );
    if ( env.processingOver() || env.errorRaised() )
    {
      _bindingRegistry.clear();
    }
    return true;
  }

  private void verifyDependencyElements( @Nonnull final RoundEnvironment env,
                                         @Nonnull final Set<? extends Element> elements )
  {
    for ( final Element element : elements )
    {
      if ( ElementKind.PARAMETER == element.getKind() )
      {
        final Element executableElement = element.getEnclosingElement();
        final boolean injectableType =
          AnnotationsUtil.hasAnnotationOfType( executableElement.getEnclosingElement(),
                                               Constants.INJECTABLE_CLASSNAME );
        final boolean moduleType =
          AnnotationsUtil.hasAnnotationOfType( executableElement.getEnclosingElement(), Constants.MODULE_CLASSNAME );
        final ElementKind executableKind = executableElement.getKind();
        if ( !injectableType && ElementKind.CONSTRUCTOR == executableKind )
        {
          reportError( env,
                       MemberChecks.must( Constants.DEPENDENCY_CLASSNAME,
                                          "only be present on a parameter of a constructor " +
                                          "if the enclosing type is annotated with " +
                                          MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME ) ),
                       element );
        }
        else if ( !moduleType && ElementKind.METHOD == executableKind )
        {
          reportError( env,
                       MemberChecks.must( Constants.DEPENDENCY_CLASSNAME,
                                          "only be present on a parameter of a method " +
                                          "if the enclosing type is annotated with " +
                                          MemberChecks.toSimpleName( Constants.MODULE_CLASSNAME ) ),
                       element );
        }
        else
        {
          assert ( injectableType && ElementKind.CONSTRUCTOR == executableKind ) ||
                 ( moduleType && ElementKind.METHOD == executableKind );
        }
      }
      else
      {
        assert ElementKind.METHOD == element.getKind();
        if ( !AnnotationsUtil.hasAnnotationOfType( element.getEnclosingElement(), Constants.INJECTOR_CLASSNAME ) )
        {
          reportError( env,
                       MemberChecks.mustNot( Constants.DEPENDENCY_CLASSNAME,
                                             "be a method unless present in a type annotated with " +
                                             MemberChecks.toSimpleName( Constants.INJECTOR_CLASSNAME ) ),
                       element );
        }
      }
    }
  }

  private void processInjectable( @Nonnull final TypeElement element )
    throws Exception
  {
    if ( ElementKind.CLASS != element.getKind() )
    {
      throw new ProcessorException( MemberChecks.must( Constants.INJECTABLE_CLASSNAME, "be a class" ),
                                    element );
    }
    else if ( element.getModifiers().contains( Modifier.ABSTRACT ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME, "be abstract" ),
                                    element );
    }
    else if ( ElementsUtil.isEnclosedInNonStaticClass( element ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                          "be a non-static nested class" ),
                                    element );
    }
    else if ( !element.getTypeParameters().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME, "have type parameters" ),
                                    element );
    }
    final List<TypeMirror> types =
      AnnotationsUtil.getTypeMirrorsAnnotationParameter( element, Constants.INJECTABLE_CLASSNAME, "types" );
    final List<TypeMirror> publishedTypes;
    if ( isDefaultTypes( types ) )
    {
      publishedTypes = Collections.singletonList( element.asType() );
    }
    else
    {
      for ( final TypeMirror type : types )
      {
        if ( !processingEnv.getTypeUtils().isAssignable( element.asType(), type ) )
        {
          throw new ProcessorException( MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME ) +
                                        " target has a type parameter containing the value " + type +
                                        " that is not assignable to the declaring type",
                                        element );
        }
      }
      publishedTypes = types;
    }
    final List<ExecutableElement> constructors = ElementsUtil.getConstructors( element );
    final ExecutableElement constructor = constructors.get( 0 );
    if ( constructors.size() > 1 )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                          "have multiple constructors" ),
                                    element );
    }
    constructorMustNotBeProtected( constructor );
    constructorMustNotBePublic( constructor );

    final String qualifier =
      (String) AnnotationsUtil.getAnnotationValue( element, Constants.INJECTABLE_CLASSNAME, "qualifier" ).getValue();
    final boolean eager =
      (boolean) AnnotationsUtil.getAnnotationValue( element, Constants.INJECTABLE_CLASSNAME, "eager" ).getValue();

    final List<DependencyDescriptor> dependencies = new ArrayList<>();
    int index = 0;
    final List<? extends TypeMirror> parameterTypes = ( (ExecutableType) constructor.asType() ).getParameterTypes();
    for ( final VariableElement parameter : constructor.getParameters() )
    {
      dependencies.add( handleConstructorParameter( parameter, parameterTypes.get( index++ ) ) );
    }
    if ( publishedTypes.isEmpty() && !eager )
    {
      throw new ProcessorException( MemberChecks.must( Constants.INJECTABLE_CLASSNAME,
                                                       "have one or more types specified or must specify eager = true otherwise the binding will never be used by the injector" ),
                                    element );
    }
    final Binding binding =
      new Binding( Binding.Type.INJECTABLE,
                   qualifier,
                   publishedTypes.toArray( new TypeMirror[ 0 ] ),
                   eager,
                   element,
                   dependencies.toArray( new DependencyDescriptor[ 0 ] ) );
    _bindingRegistry.registerBinding( binding );
    binding.write( processingEnv );
  }

  @Nonnull
  private DependencyDescriptor handleConstructorParameter( @Nonnull final VariableElement parameter,
                                                           @Nonnull final TypeMirror parameterType )
  {
    final boolean optional =
      AnnotationsUtil.hasAnnotationOfType( parameter, GeneratorUtil.NULLABLE_ANNOTATION_CLASSNAME );
    final AnnotationMirror annotation =
      AnnotationsUtil.findAnnotationByType( parameter, Constants.DEPENDENCY_CLASSNAME );
    final String qualifier =
      null == annotation ? "" : AnnotationsUtil.getAnnotationValue( annotation, "qualifier" );

    final TypeName typeName = TypeName.get( parameterType );
    final boolean isParameterizedType = typeName instanceof ParameterizedTypeName;
    final DependencyDescriptor.Type type;
    final TypeMirror dependencyValueType;
    if ( typeName instanceof ClassName )
    {
      if ( StingTypeNames.SUPPLIER.equals( typeName ) )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                            "have a constructor parameter that is a raw " +
                                                            StingTypeNames.SUPPLIER + " type" ),
                                      parameter );
      }
    }
    else if ( typeName instanceof ParameterizedTypeName )
    {
      final ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) typeName;
      if ( StingTypeNames.SUPPLIER.equals( parameterizedTypeName.rawType ) &&
           parameterizedTypeName.typeArguments.get( 0 ) instanceof WildcardTypeName )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                            "have a constructor parameter that is a " +
                                                            StingTypeNames.SUPPLIER +
                                                            " type with a wildcard parameter" ),
                                      parameter );
      }
    }
    if ( isParameterizedType )
    {
      type = DependencyDescriptor.Type.SUPPLIER;
      dependencyValueType = ( (DeclaredType) parameterType ).getTypeArguments().get( 0 );
    }
    else
    {
      type = DependencyDescriptor.Type.INSTANCE;
      dependencyValueType = parameterType;
    }

    final Coordinate coordinate = new Coordinate( qualifier, dependencyValueType );
    return new DependencyDescriptor( type, coordinate, optional, parameter );
  }

  private boolean isDefaultTypes( @Nonnull final List<TypeMirror> types )
  {
    return 1 == types.size() && TypeKind.VOID == types.get( 0 ).getKind();
  }

  private void constructorMustNotBePublic( @Nonnull final ExecutableElement constructor )
  {
    if ( !isSynthetic( constructor ) &&
         constructor.getModifiers().contains( Modifier.PUBLIC ) &&
         ElementsUtil.isWarningNotSuppressed( constructor, Constants.WARNING_PUBLIC_CONSTRUCTOR ) )
    {
      final String message =
        MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME ) + " target should not have a public " +
        "constructor. The type is instantiated by the injector and should have a package-access constructor. " +
        MemberChecks.suppressedBy( Constants.WARNING_PUBLIC_CONSTRUCTOR );
      processingEnv.getMessager().printMessage( Diagnostic.Kind.WARNING, message, constructor );
    }
  }

  private void constructorMustNotBeProtected( @Nonnull final ExecutableElement constructor )
  {
    if ( constructor.getModifiers().contains( Modifier.PROTECTED ) &&
         ElementsUtil.isWarningNotSuppressed( constructor, Constants.WARNING_PROTECTED_CONSTRUCTOR ) )
    {
      final String message =
        MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME ) + " target should not have a protected " +
        "constructor. The type is instantiated by the injector and should have a package-access constructor. " +
        MemberChecks.suppressedBy( Constants.WARNING_PROTECTED_CONSTRUCTOR );
      processingEnv.getMessager().printMessage( Diagnostic.Kind.WARNING, message, constructor );
    }
  }

  /**
   * Returns true if the given element is synthetic.
   *
   * @param element to check
   * @return true if and only if the given element is synthetic, false otherwise
   */
  private boolean isSynthetic( @Nonnull final Element element )
  {
    final long flags = ( (Symbol) element ).flags();
    return 0 != ( flags & Flags.SYNTHETIC ) || 0 != ( flags & Flags.GENERATEDCONSTR );
  }
}
