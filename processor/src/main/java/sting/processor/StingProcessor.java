package sting.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
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
import org.realityforge.proton.SuperficialValidation;

/**
 * Annotation processor that analyzes sting annotated source and generates dependency injection container.
 */
@SuppressWarnings( "DuplicatedCode" )
@SupportedAnnotationTypes( { Constants.INJECTOR_CLASSNAME,
                             Constants.INJECTABLE_CLASSNAME,
                             Constants.FRAGMENT_CLASSNAME,
                             Constants.DEPENDENCY_CLASSNAME } )
@SupportedSourceVersion( SourceVersion.RELEASE_8 )
@SupportedOptions( { "sting.defer.unresolved", "sting.defer.errors" } )
public final class StingProcessor
  extends AbstractStandardProcessor
{
  /**
   * Extension for json descriptors.
   */
  private static final String DESCRIPTOR_FILE_SUFFIX = ".sting.json";
  /**
   * A local cache of bindings that is cleared on error or when processing is complete.
   * This will probably be loaded from json cache files in the future but now we require
   * in memory processing.
   */
  @Nonnull
  private final Registry _registry = new Registry();

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
      .filter( a -> a.getQualifiedName().toString().equals( Constants.FRAGMENT_CLASSNAME ) )
      .findAny()
      .ifPresent( a -> processTypeElements( env,
                                            (Collection<TypeElement>) env.getElementsAnnotatedWith( a ),
                                            this::processFragment ) );

    annotations.stream()
      .filter( a -> a.getQualifiedName().toString().equals( Constants.DEPENDENCY_CLASSNAME ) )
      .findAny()
      .ifPresent( a -> verifyDependencyElements( env, env.getElementsAnnotatedWith( a ) ) );

    annotations.stream()
      .filter( a -> a.getQualifiedName().toString().equals( Constants.INJECTOR_CLASSNAME ) )
      .findAny()
      .ifPresent( a -> processTypeElements( env,
                                            (Collection<TypeElement>) env.getElementsAnnotatedWith( a ),
                                            this::processInjector ) );

    processResolvedInjectors( env );

    errorIfProcessingOverAndInvalidTypesDetected( env );
    errorIfProcessingOverAndUnprocessedInjectorDetected( env );
    if ( env.processingOver() || env.errorRaised() )
    {
      _registry.clear();
    }
    return true;
  }

  private void errorIfProcessingOverAndUnprocessedInjectorDetected( @Nonnull final RoundEnvironment env )
  {
    if ( env.processingOver() && !env.errorRaised() )
    {
      final List<InjectorDescriptor> injectors = _registry.getInjectors();
      if ( !injectors.isEmpty() )
      {
        processingEnv
          .getMessager()
          .printMessage( Diagnostic.Kind.ERROR,
                         getClass().getSimpleName() + " failed to process " + injectors.size() + " injectors " +
                         "as not all of their dependencies could be resolved. Check for compilation errors or a " +
                         "circular dependency with generated code." );
        for ( final InjectorDescriptor injector : injectors )
        {
          processingEnv
            .getMessager()
            .printMessage( Diagnostic.Kind.ERROR,
                           "Failed to process " + injector.getElement().getQualifiedName() + " injector " +
                           "as not all of the dependencies could be resolved." );
        }
      }
    }
  }

  private void processResolvedInjectors( @Nonnull final RoundEnvironment env )
  {
    for ( final InjectorDescriptor injector : new ArrayList<>( _registry.getInjectors() ) )
    {
      if ( isInjectorResolved( env, injector ) )
      {
        performAction( env, e -> buildAndEmitObjectGraph( injector ), injector.getElement() );
        _registry.deregisterInjector( injector );
      }
    }
  }

  private void buildAndEmitObjectGraph( @Nonnull final InjectorDescriptor injector )
    throws Exception
  {
    final ObjectGraph graph = new ObjectGraph( injector );
    registerIncludesComponents( graph );

    buildObjectGraphNodes( graph );

    graph.getNodes().stream().filter( n -> n.getBinding().isEager() ).forEach( Node::markNodeAndUpstreamAsEager );

    //TODO: Assign depth metric for each node which is distance from root dependencies
    //TODO: Make sure graph has no circular loops

    emitObjectGraphDescriptor( graph );

    //TODO: Generate and emit java code
  }

  private void registerIncludesComponents( @Nonnull final ObjectGraph graph )
  {
    final InjectorDescriptor injector = graph.getInjector();
    for ( final DeclaredType include : injector.getIncludes() )
    {
      final TypeElement element = (TypeElement) include.asElement();
      if ( AnnotationsUtil.hasAnnotationOfType( element, Constants.FRAGMENT_CLASSNAME ) )
      {
        graph.registerFragment( _registry.getFragmentByClassName( element.getQualifiedName().toString() ) );
      }
      else if ( AnnotationsUtil.hasAnnotationOfType( element, Constants.INJECTABLE_CLASSNAME ) )
      {
        //TODO: Recursively process includes here
        graph.registerInjectable( _registry.getInjectableByClassName( element.getQualifiedName().toString() ) );
      }
      else
      {
        assert AnnotationsUtil.hasAnnotationOfType( element, Constants.FACTORY_CLASSNAME );
        //TODO: Add support for factory
      }
    }
  }

  private void buildObjectGraphNodes( @Nonnull final ObjectGraph graph )
  {
    final InjectorDescriptor injector = graph.getInjector();
    final Set<Node> completed = new HashSet<>();
    final Stack<Edge> workList = new Stack<>();
    workList.addAll( graph.getRootNode().getDependsOn() );
    while ( !workList.isEmpty() )
    {
      final Edge edge = workList.pop();
      final DependencyDescriptor dependency = edge.getDependency();
      final Coordinate coordinate = dependency.getCoordinate();
      final List<Binding> bindings = new ArrayList<>( graph.findAllBindingsByCoordinate( coordinate ) );

      if ( bindings.isEmpty() )
      {
        //TODO: This means that only if the @Injectable was compiled in the same compilation. We really should
        // instead try to look up injectable descriptor if not compiled already.
        final InjectableDescriptor injectable = _registry.findInjectableByClassName( coordinate.getType().toString() );
        if ( null != injectable && injectable.getBinding().getCoordinates().contains( coordinate ) )
        {
          bindings.add( injectable.getBinding() );
        }
      }

      final List<Binding> nullableProviders = bindings.stream()
        .filter( b -> Binding.Type.NULLABLE_PROVIDES == b.getBindingType() )
        .collect( Collectors.toList() );
      if ( !dependency.isOptional() && !nullableProviders.isEmpty() )
      {
        throw new ProcessorException( "@Nullable annotated provider method is attempting to satisfy non-optional " +
                                      "dependency " + coordinate + " for injector defined by type " +
                                      injector.getElement().getQualifiedName() + ".\n\nPath to dependency: " +
                                      getPath( injector, edge ) + ".\n\n@Nullable annotated provider methods: " +
                                      describeProviderMethods( nullableProviders ),
                                      dependency.getElement() );
      }
      if ( bindings.isEmpty() )
      {
        if ( dependency.isOptional() )
        {
          edge.setSatisfiedBy( Collections.emptyList() );
        }
        else
        {
          throw new ProcessorException( "Unable to satisfy non-optional dependency " + coordinate +
                                        " for injector defined by type " + injector.getElement().getQualifiedName() +
                                        ". Path to dependency: " + getPath( injector, edge ),
                                        dependency.getElement() );
        }
      }
      else
      {
        final DependencyDescriptor.Type type = dependency.getType();
        if ( 1 == bindings.size() || type.isCollection() )
        {
          final List<Node> nodes = bindings.stream().map( graph::findOrCreateNode ).collect( Collectors.toList() );
          for ( final Node node : nodes )
          {
            if ( !completed.contains( node ) )
            {
              completed.add( node );
              workList.addAll( node.getDependsOn() );
            }
          }
          edge.setSatisfiedBy( nodes );
        }
        else
        {
          //noinspection ConstantConditions
          assert bindings.size() > 1 && !type.isCollection();
          throw new ProcessorException( "Dependency that expects a single value to satisfy dependency can be " +
                                        "satisfied by multiple values in injector defined by type " +
                                        injector.getElement().getQualifiedName() +
                                        ". Path to dependency: " + getPath( injector, edge ) + ". Bindings:",
                                        dependency.getElement() );
        }
      }
    }
  }

  private void emitObjectGraphDescriptor( @Nonnull final ObjectGraph graph )
    throws IOException
  {
    final TypeElement element = graph.getInjector().getElement();
    final String filename = toFilename( element ) + "__ObjectGraph" + DESCRIPTOR_FILE_SUFFIX;
    JsonUtil.writeJsonResource( processingEnv, element, filename, graph::write );
  }

  @Nonnull
  private String describeProviderMethods( final List<Binding> providers )
  {
    return providers.stream().map( binding -> {
      final Binding.Type bindingType = binding.getBindingType();
      assert Binding.Type.PROVIDES == bindingType || Binding.Type.NULLABLE_PROVIDES == bindingType;
      final StringWriter sw = new StringWriter();
      processingEnv.getElementUtils().printElements( sw, binding.getElement() );
      return sw.toString();
    } ).collect( Collectors.joining( "\n---\n" ) );
  }

  private String getPath( @Nonnull final InjectorDescriptor injector, @Nonnull final Edge edge )
  {
    return "TODO";
  }

  private boolean isInjectorResolved( @Nonnull final RoundEnvironment env, @Nonnull final InjectorDescriptor injector )
  {
    for ( final DeclaredType include : injector.getIncludes() )
    {
      if ( !SuperficialValidation.validateType( processingEnv, include ) )
      {
        return false;
      }
      else
      {
        final TypeElement element = (TypeElement) include.asElement();
        if ( AnnotationsUtil.hasAnnotationOfType( element, Constants.FRAGMENT_CLASSNAME ) )
        {
          final String classname = element.getQualifiedName().toString();
          FragmentDescriptor fragment = _registry.findFragmentByClassName( classname );
          if ( null == fragment )
          {
            // TODO: In the future we should attempt to load binary descriptor here
            //  rather than analyzing element. If binary descriptor is missing then we
            //  should just return false and assume it will be picked up in a future round
            performAction( env, this::processFragment, element );
            fragment = _registry.findFragmentByClassName( classname );
            if ( null == fragment )
            {
              return false;
            }
          }
        }
        else if ( AnnotationsUtil.hasAnnotationOfType( element, Constants.INJECTABLE_CLASSNAME ) )
        {
          final String classname = element.getQualifiedName().toString();
          InjectableDescriptor injectable = _registry.findInjectableByClassName( classname );
          if ( null == injectable )
          {
            // TODO: In the future we should attempt to load binary descriptor here
            //  rather than analyzing element. If binary descriptor is missing then we
            //  should just return false and assume it will be picked up in a future round
            performAction( env, this::processFragment, element );
            injectable = _registry.findInjectableByClassName( classname );
            if ( null == injectable )
            {
              return false;
            }
          }
        }
        else
        {
          assert AnnotationsUtil.hasAnnotationOfType( element, Constants.FACTORY_CLASSNAME );
          //TODO: Add support for factory
        }
      }
    }
    return true;
  }

  private void processInjector( @Nonnull final TypeElement element )
    throws Exception
  {
    final ElementKind kind = element.getKind();
    if ( ElementKind.INTERFACE != kind && ElementKind.CLASS != kind )
    {
      throw new ProcessorException( MemberChecks.must( Constants.INJECTOR_CLASSNAME,
                                                       "be an interface or an abstract class" ),
                                    element );
    }
    else if ( ElementKind.CLASS == kind && !element.getModifiers().contains( Modifier.ABSTRACT ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                                          "must be abstract if the target is a class" ),
                                    element );
    }
    if ( ElementKind.CLASS == kind )
    {
      final List<ExecutableElement> constructors = ElementsUtil.getConstructors( element );
      final ExecutableElement constructor = constructors.get( 0 );
      if ( constructors.size() > 1 )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                                            "have multiple constructors" ),
                                      element );
      }
      injectorConstructorMustNotBeProtected( constructor );
      injectorConstructorMustNotBePublic( constructor );
    }

    final List<DeclaredType> includes = extractIncludes( element, Constants.INJECTOR_CLASSNAME );

    final List<DependencyDescriptor> topLevelDependencies = new ArrayList<>();
    final List<ExecutableElement> methods =
      ElementsUtil.getMethods( element, processingEnv.getElementUtils(), processingEnv.getTypeUtils() );
    for ( final ExecutableElement method : methods )
    {
      if ( method.getModifiers().contains( Modifier.ABSTRACT ) )
      {
        processInjectorDependencyMethod( topLevelDependencies, method );
      }
    }
    final InjectorDescriptor injector = new InjectorDescriptor( element, includes, topLevelDependencies );
    _registry.registerInjector( injector );
    emitInjectorDescriptor( injector );
  }

  private void emitInjectorDescriptor( @Nonnull final InjectorDescriptor injector )
    throws IOException
  {
    final TypeElement element = injector.getElement();
    final String filename = toFilename( element ) + DESCRIPTOR_FILE_SUFFIX;
    JsonUtil.writeJsonResource( processingEnv, element, filename, injector::write );
  }

  private void injectorConstructorMustNotBePublic( @Nonnull final ExecutableElement constructor )
  {
    if ( isNotSynthetic( constructor ) &&
         constructor.getModifiers().contains( Modifier.PUBLIC ) &&
         ElementsUtil.isWarningNotSuppressed( constructor, Constants.WARNING_PUBLIC_CONSTRUCTOR ) )
    {
      final String message =
        MemberChecks.toSimpleName( Constants.INJECTOR_CLASSNAME ) + " target should not have a public " +
        "constructor. The type should not be directly instantiated and should have a protected or package-access " +
        "constructor. " + MemberChecks.suppressedBy( Constants.WARNING_PUBLIC_CONSTRUCTOR );
      processingEnv.getMessager().printMessage( Diagnostic.Kind.WARNING, message, constructor );
    }
  }

  private void injectorConstructorMustNotBeProtected( @Nonnull final ExecutableElement constructor )
  {
    if ( !constructor.getEnclosingElement().getModifiers().contains( Modifier.PUBLIC ) &&
         constructor.getModifiers().contains( Modifier.PROTECTED ) &&
         ElementsUtil.isWarningNotSuppressed( constructor, Constants.WARNING_PROTECTED_CONSTRUCTOR ) )
    {
      final String message =
        MemberChecks.toSimpleName( Constants.INJECTOR_CLASSNAME ) +
        " target should not have a protected " +
        "constructor when the type is not public. The constructor is only invoked from subclasses that must be " +
        "package-access as the type is not public. " +
        MemberChecks.suppressedBy( Constants.WARNING_PROTECTED_CONSTRUCTOR );
      processingEnv.getMessager().printMessage( Diagnostic.Kind.WARNING, message, constructor );
    }
  }

  private void processInjectorDependencyMethod( @Nonnull final List<DependencyDescriptor> topLevelDependencies,
                                                @Nonnull final ExecutableElement method )
  {
    assert method.getModifiers().contains( Modifier.ABSTRACT );
    MemberChecks.mustReturnAValue( Constants.DEPENDENCY_CLASSNAME, method );
    MemberChecks.mustNotHaveAnyParameters( Constants.DEPENDENCY_CLASSNAME, method );
    MemberChecks.mustNotHaveAnyTypeParameters( Constants.DEPENDENCY_CLASSNAME, method );
    topLevelDependencies.add( processDependencyMethod( method ) );
  }

  @Nonnull
  private DependencyDescriptor processDependencyMethod( @Nonnull final ExecutableElement method )
  {
    final TypeMirror returnType = method.getReturnType();
    final boolean optional =
      AnnotationsUtil.hasAnnotationOfType( method, GeneratorUtil.NULLABLE_ANNOTATION_CLASSNAME );
    final AnnotationMirror annotation =
      AnnotationsUtil.findAnnotationByType( method, Constants.DEPENDENCY_CLASSNAME );
    final String qualifier =
      null == annotation ? "" : AnnotationsUtil.getAnnotationValue( annotation, "qualifier" );

    final TypeName typeName = TypeName.get( returnType );
    final boolean isParameterizedType = typeName instanceof ParameterizedTypeName;
    final DependencyDescriptor.Type type;
    final TypeMirror dependencyValueType;
    if ( typeName instanceof ClassName )
    {
      if ( StingTypeNames.SUPPLIER.equals( typeName ) )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.DEPENDENCY_CLASSNAME,
                                                            "return a value that is a raw " +
                                                            StingTypeNames.SUPPLIER + " type" ),
                                      method );
      }
      else if ( !( (TypeElement) ( (DeclaredType) returnType ).asElement() ).getTypeParameters().isEmpty() )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.DEPENDENCY_CLASSNAME,
                                                            "return a value that is a raw parameterized " +
                                                            "type. Parameterized types are only permitted for " +
                                                            "specific types such as " + StingTypeNames.SUPPLIER ),
                                      method );
      }
    }
    else if ( typeName instanceof ParameterizedTypeName )
    {
      final ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) typeName;
      if ( StingTypeNames.SUPPLIER.equals( parameterizedTypeName.rawType ) )
      {
        if ( parameterizedTypeName.typeArguments.get( 0 ) instanceof WildcardTypeName )
        {
          throw new ProcessorException( MemberChecks.mustNot( Constants.DEPENDENCY_CLASSNAME,
                                                              "return a value that is a " +
                                                              StingTypeNames.SUPPLIER +
                                                              " type with a wildcard parameter" ),
                                        method );
        }
      }
      else
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.DEPENDENCY_CLASSNAME,
                                                            "return a value that is a parameterized type. " +
                                                            "This is only permitted for specific types such as " +
                                                            StingTypeNames.SUPPLIER ),
                                      method );
      }
    }
    if ( isParameterizedType )
    {
      type = DependencyDescriptor.Type.SUPPLIER;
      dependencyValueType = ( (DeclaredType) returnType ).getTypeArguments().get( 0 );
    }
    else
    {
      type = DependencyDescriptor.Type.INSTANCE;
      dependencyValueType = returnType;
    }

    final Coordinate coordinate = new Coordinate( qualifier, dependencyValueType );
    return new DependencyDescriptor( type, coordinate, optional, method );
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
        final boolean isFragmentType =
          AnnotationsUtil.hasAnnotationOfType( executableElement.getEnclosingElement(), Constants.FRAGMENT_CLASSNAME );
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
        else if ( !isFragmentType && ElementKind.METHOD == executableKind )
        {
          reportError( env,
                       MemberChecks.must( Constants.DEPENDENCY_CLASSNAME,
                                          "only be present on a parameter of a method " +
                                          "if the enclosing type is annotated with " +
                                          MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME ) ),
                       element );
        }
        else
        {
          assert ( injectableType && ElementKind.CONSTRUCTOR == executableKind ) ||
                 ( isFragmentType && ElementKind.METHOD == executableKind );
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

  private void processFragment( @Nonnull final TypeElement element )
    throws Exception
  {
    if ( ElementKind.INTERFACE != element.getKind() )
    {
      throw new ProcessorException( MemberChecks.must( Constants.FRAGMENT_CLASSNAME, "be an interface" ),
                                    element );
    }
    else if ( !element.getTypeParameters().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME, "have type parameters" ),
                                    element );
    }
    final List<DeclaredType> includes = extractIncludes( element, Constants.FRAGMENT_CLASSNAME );
    final Map<ExecutableElement, Binding> bindings = new LinkedHashMap<>();
    final List<ExecutableElement> methods =
      ElementsUtil.getMethods( element, processingEnv.getElementUtils(), processingEnv.getTypeUtils() );
    for ( final ExecutableElement method : methods )
    {
      processProvidesMethod( element, bindings, method );
    }
    if ( bindings.isEmpty() && includes.isEmpty() )
    {
      throw new ProcessorException( MemberChecks.must( Constants.FRAGMENT_CLASSNAME,
                                                       "contain one or more methods or one or more includes" ),
                                    element );
    }
    final FragmentDescriptor fragment = new FragmentDescriptor( element, includes, bindings.values() );
    _registry.registerFragment( fragment );
    emitFragmentDescriptor( fragment );
  }

  @Nonnull
  private List<DeclaredType> extractIncludes( @Nonnull final TypeElement element,
                                              @Nonnull final String annotationClassname )
  {
    final List<DeclaredType> results = new ArrayList<>();
    final List<TypeMirror> includes =
      AnnotationsUtil.getTypeMirrorsAnnotationParameter( element, annotationClassname, "includes" );
    for ( final TypeMirror include : includes )
    {
      final Element includeElement = processingEnv.getTypeUtils().asElement( include );
      if ( !AnnotationsUtil.hasAnnotationOfType( includeElement, Constants.FRAGMENT_CLASSNAME ) &&
           !AnnotationsUtil.hasAnnotationOfType( includeElement, Constants.INJECTABLE_CLASSNAME ) &&
           !AnnotationsUtil.hasAnnotationOfType( includeElement, Constants.FACTORY_CLASSNAME ) )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( annotationClassname ) + " target has an " +
                                      "includes parameter containing the value " + include + " that is not a type " +
                                      "annotated by either " +
                                      MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME ) + ", " +
                                      MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME ) + " or " +
                                      MemberChecks.toSimpleName( Constants.FACTORY_CLASSNAME ),
                                      element );
      }
      else
      {
        results.add( (DeclaredType) include );
      }
    }
    return results;
  }

  private void emitFragmentDescriptor( @Nonnull final FragmentDescriptor fragment )
    throws IOException
  {
    final TypeElement element = fragment.getElement();
    final String filename = toFilename( element ) + DESCRIPTOR_FILE_SUFFIX;
    JsonUtil.writeJsonResource( processingEnv, element, filename, fragment::write );
  }

  private void processProvidesMethod( @Nonnull final TypeElement element,
                                      @Nonnull final Map<ExecutableElement, Binding> bindings,
                                      @Nonnull final ExecutableElement method )
  {
    MemberChecks.mustReturnAValue( Constants.PROVIDES_CLASSNAME, method );
    MemberChecks.mustNotHaveAnyTypeParameters( Constants.PROVIDES_CLASSNAME, method );
    if ( !method.getModifiers().contains( Modifier.DEFAULT ) )
    {
      throw new ProcessorException( MemberChecks.must( Constants.PROVIDES_CLASSNAME, "have a default modifier" ),
                                    method );
    }
    else
    {
      final boolean providesPresent = AnnotationsUtil.hasAnnotationOfType( method, Constants.PROVIDES_CLASSNAME );
      final boolean nullablePresent =
        AnnotationsUtil.hasAnnotationOfType( method, GeneratorUtil.NULLABLE_ANNOTATION_CLASSNAME );
      final List<TypeMirror> types =
        providesPresent ?
        AnnotationsUtil.getTypeMirrorsAnnotationParameter( method, Constants.PROVIDES_CLASSNAME, "types" ) :
        Collections.emptyList();
      final List<TypeMirror> publishedTypes;
      if ( !providesPresent || isDefaultTypes( types ) )
      {
        publishedTypes = Collections.singletonList( method.getReturnType() );
      }
      else
      {
        for ( final TypeMirror type : types )
        {
          if ( !processingEnv.getTypeUtils().isAssignable( method.getReturnType(), type ) )
          {
            throw new ProcessorException( MemberChecks.toSimpleName( Constants.PROVIDES_CLASSNAME ) +
                                          " target has a type parameter containing the value " + type +
                                          " that is not assignable to the return type of the method",
                                          method );
          }
        }
        publishedTypes = types;
      }
      final String qualifier =
        providesPresent ?
        (String) AnnotationsUtil.getAnnotationValue( method, Constants.PROVIDES_CLASSNAME, "qualifier" )
          .getValue() :
        "";
      final boolean eager =
        providesPresent &&
        (boolean) AnnotationsUtil.getAnnotationValue( method, Constants.PROVIDES_CLASSNAME, "eager" ).getValue();
      final String declaredId =
        providesPresent ?
        (String) AnnotationsUtil.getAnnotationValue( method, Constants.PROVIDES_CLASSNAME, "id" ).getValue() :
        "";
      final String id = declaredId.isEmpty() ? element.getQualifiedName() + "#" + method.getSimpleName() : declaredId;

      final List<DependencyDescriptor> dependencies = new ArrayList<>();
      int index = 0;
      final List<? extends TypeMirror> parameterTypes = ( (ExecutableType) method.asType() ).getParameterTypes();
      for ( final VariableElement parameter : method.getParameters() )
      {
        final TypeMirror parameterType = parameterTypes.get( index++ );
        dependencies.add( processFragmentDependencyParameter( parameter, parameterType ) );
      }
      if ( publishedTypes.isEmpty() && !eager )
      {
        throw new ProcessorException( MemberChecks.must( Constants.PROVIDES_CLASSNAME,
                                                         "have one or more types specified or must specify eager = true otherwise the binding will never be used by the injector" ),
                                      element );
      }
      bindings.entrySet()
        .stream()
        .filter( e -> e.getValue().getId().equals( id ) )
        .map( Map.Entry::getKey )
        .findAny()
        .ifPresent( matchingMethod -> {
          throw new ProcessorException( MemberChecks.must( Constants.PROVIDES_CLASSNAME,
                                                           "have a unique id but it has the same id as the method named " +
                                                           matchingMethod.getSimpleName() ),
                                        element );

        } );

      final Binding binding =
        new Binding( nullablePresent ? Binding.Type.NULLABLE_PROVIDES : Binding.Type.PROVIDES,
                     id,
                     qualifier,
                     publishedTypes.toArray( new TypeMirror[ 0 ] ),
                     eager,
                     method,
                     dependencies.toArray( new DependencyDescriptor[ 0 ] ) );
      bindings.put( method, binding );
    }
  }

  @Nonnull
  private DependencyDescriptor processFragmentDependencyParameter( @Nonnull final VariableElement parameter,
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
        throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                            "have a method with a parameter that is a raw " +
                                                            StingTypeNames.SUPPLIER + " type" ),
                                      parameter );
      }
      else if ( !( (TypeElement) ( (DeclaredType) parameterType ).asElement() ).getTypeParameters().isEmpty() )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                            "have a method with a parameter that is a " +
                                                            "raw parameterized type. Parameterized types are only " +
                                                            "permitted for specific types such as " +
                                                            StingTypeNames.SUPPLIER ),
                                      parameter );
      }
    }
    else if ( typeName instanceof ParameterizedTypeName )
    {
      final ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) typeName;
      if ( StingTypeNames.SUPPLIER.equals( parameterizedTypeName.rawType ) )
      {
        if ( parameterizedTypeName.typeArguments.get( 0 ) instanceof WildcardTypeName )
        {
          throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                              "have a method with a parameter that is a " +
                                                              StingTypeNames.SUPPLIER +
                                                              " type with a wildcard parameter" ),
                                        parameter );
        }
      }
      else
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                            "have a method with a parameter that is a " +
                                                            "parameterized type. This is only permitted for " +
                                                            "specific types such as " + StingTypeNames.SUPPLIER ),
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
    else if ( ElementsUtil.isNonStaticNestedClass( element ) )
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
    injectableConstructorMustNotBeProtected( constructor );
    injectableConstructorMustNotBePublic( constructor );

    final String declaredId =
      (String) AnnotationsUtil.getAnnotationValue( element, Constants.INJECTABLE_CLASSNAME, "id" ).getValue();
    final String id = declaredId.isEmpty() ? element.getQualifiedName().toString() : declaredId;
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
                   id,
                   qualifier,
                   publishedTypes.toArray( new TypeMirror[ 0 ] ),
                   eager,
                   element,
                   dependencies.toArray( new DependencyDescriptor[ 0 ] ) );
    final InjectableDescriptor injectable = new InjectableDescriptor( binding );
    _registry.registerInjectable( injectable );
    final String filename = toFilename( element ) + DESCRIPTOR_FILE_SUFFIX;
    JsonUtil.writeJsonResource( processingEnv, element, filename, injectable::write );
  }

  @Nonnull
  private String toFilename( @Nonnull final TypeElement typeElement )
  {
    return GeneratorUtil.getGeneratedClassName( typeElement, "", "" ).toString().replace( ".", "/" );
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
                                                            "have a constructor with a parameter that is " +
                                                            "a raw " + StingTypeNames.SUPPLIER + " type" ),
                                      parameter );
      }
      else if ( !( (TypeElement) ( (DeclaredType) parameterType ).asElement() ).getTypeParameters().isEmpty() )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                            "have a constructor with a parameter that is a " +
                                                            "raw parameterized type. Parameterized types are only " +
                                                            "permitted for specific types such as " +
                                                            StingTypeNames.SUPPLIER ),
                                      parameter );
      }
    }
    else if ( typeName instanceof ParameterizedTypeName )
    {
      final ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) typeName;
      if ( StingTypeNames.SUPPLIER.equals( parameterizedTypeName.rawType ) )
      {
        if ( parameterizedTypeName.typeArguments.get( 0 ) instanceof WildcardTypeName )
        {
          throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                              "have a constructor with a parameter " +
                                                              "that is a " + StingTypeNames.SUPPLIER +
                                                              " type with a wildcard parameter" ),
                                        parameter );
        }
      }
      else
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                            "have a constructor with a parameter that is a " +
                                                            "parameterized type. This is only permitted for " +
                                                            "specific types such as " + StingTypeNames.SUPPLIER ),
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

  private void injectableConstructorMustNotBePublic( @Nonnull final ExecutableElement constructor )
  {
    if ( isNotSynthetic( constructor ) &&
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

  private void injectableConstructorMustNotBeProtected( @Nonnull final ExecutableElement constructor )
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
  private boolean isNotSynthetic( @Nonnull final Element element )
  {
    final long flags = ( (Symbol) element ).flags();
    return 0 == ( flags & Flags.SYNTHETIC ) && 0 == ( flags & Flags.GENERATEDCONSTR );
  }
}
