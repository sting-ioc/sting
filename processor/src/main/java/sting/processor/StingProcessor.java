package sting.processor;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
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
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import org.realityforge.proton.AbstractStandardProcessor;
import org.realityforge.proton.AnnotationsUtil;
import org.realityforge.proton.ElementsUtil;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.IOUtil;
import org.realityforge.proton.JsonUtil;
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
@SupportedOptions( { "sting.defer.unresolved",
                     "sting.defer.errors",
                     "sting.emit_json_descriptors",
                     "sting.verify_descriptors" } )
public final class StingProcessor
  extends AbstractStandardProcessor
{
  /**
   * Extension for json descriptors.
   */
  static final String JSON_SUFFIX = ".sting.json";
  /**
   * Extension for sting binary descriptors.
   */
  static final String SUFFIX = ".sbf";
  /**
   * Extension for the computed graph descriptor.
   */
  static final String GRAPH_SUFFIX = "__ObjectGraph" + JSON_SUFFIX;
  /**
   * A local cache of bindings that is cleared on error or when processing is complete.
   * This will probably be loaded from json cache files in the future but now we require
   * in memory processing.
   */
  @Nonnull
  private final Registry _registry = new Registry();
  /**
   * Flag controlling whether json descriptors are emitted.
   * Json descriptors are primarily used during debugging and probably should not be enabled in production code.
   */
  private boolean _emitJsonDescriptors;
  /**
   * Flag controlling whether the binary descriptors are deserialized after serialization to verify
   * that they produce the expected output. This is only used for debugging and should not be enabled
   * in production code.
   */
  private boolean _verifyDescriptors;
  /**
   * A utility class for reading and writing the binary descriptors.
   */
  private DescriptorIO _descriptorIO;

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

  @Override
  public synchronized void init( final ProcessingEnvironment processingEnv )
  {
    super.init( processingEnv );
    _descriptorIO = new DescriptorIO( processingEnv.getElementUtils(), processingEnv.getTypeUtils() );
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public boolean process( @Nonnull final Set<? extends TypeElement> annotations, @Nonnull final RoundEnvironment env )
  {
    _emitJsonDescriptors =
      "true".equals( processingEnv.getOptions().getOrDefault( "sting.emit_json_descriptors", "false" ) );
    _verifyDescriptors =
      "true".equals( processingEnv.getOptions().getOrDefault( "sting.verify_descriptors", "false" ) );

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
      performAction( env, e -> {
        if ( isInjectorResolved( injector ) )
        {
          _registry.deregisterInjector( injector );
          buildAndEmitObjectGraph( injector );
          _registry.deregisterInjector( injector );
        }
      }, injector.getElement() );
    }
  }

  private void buildAndEmitObjectGraph( @Nonnull final InjectorDescriptor injector )
    throws Exception
  {
    final ObjectGraph graph = new ObjectGraph( injector );
    registerIncludesComponents( graph );

    buildObjectGraphNodes( graph );

    if ( graph.getNodes().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.toSimpleName( Constants.INJECTOR_CLASSNAME ) + " target " +
                                    "produced an empty object graph. This means that there are no eager values " +
                                    "in the includes and there are no dependencies or only unsatisfied optional " +
                                    "dependencies defined by the injector",
                                    graph.getInjector().getElement() );
    }

    propagateEagerFlagUpstream( graph );

    CircularDependencyChecker.verifyNoCircularDependencyLoops( graph );

    emitObjectGraphJsonDescriptor( graph );

    //TODO: Generate and emit java code
  }

  private void propagateEagerFlagUpstream( @Nonnull final ObjectGraph graph )
  {
    // Propagate Eager flag to all dependencies of eager nodes breaking the propagation at Supplier nodes
    // They may not be configured as eager but they are effectively eager given that they will be created
    // at startup, they may as well be marked as eager objects as that results in smaller code-size.
    graph.getNodes().stream().filter( n -> n.getBinding().isEager() ).forEach( Node::markNodeAndUpstreamAsEager );
  }

  private void registerIncludesComponents( @Nonnull final ObjectGraph graph )
  {
    registerIncludes( graph, graph.getInjector().getIncludes() );
  }

  private void registerIncludes( @Nonnull final ObjectGraph graph,
                                 @Nonnull final Collection<DeclaredType> includes )
  {
    for ( final DeclaredType include : includes )
    {
      final TypeElement element = (TypeElement) include.asElement();
      if ( AnnotationsUtil.hasAnnotationOfType( element, Constants.FRAGMENT_CLASSNAME ) )
      {
        final FragmentDescriptor fragment = _registry.getFragmentByClassName( element.getQualifiedName().toString() );
        registerIncludes( graph, fragment.getIncludes() );
        graph.registerFragment( fragment );
      }
      else
      {
        assert AnnotationsUtil.hasAnnotationOfType( element, Constants.INJECTABLE_CLASSNAME );
        final InjectableDescriptor injectable =
          _registry.getInjectableByClassName( element.getQualifiedName().toString() );
        final Binding binding = injectable.getBinding();
        if ( binding.isEager() )
        {
          graph.findOrCreateNode( binding );
        }
        graph.registerInjectable( injectable );
      }
    }
  }

  private void buildObjectGraphNodes( @Nonnull final ObjectGraph graph )
  {
    final InjectorDescriptor injector = graph.getInjector();
    final Node rootNode = graph.getRootNode();
    final Set<Node> completed = new HashSet<>();
    final Stack<WorkEntry> workList = new Stack<>();
    addDependsOnToWorkList( workList, rootNode, null );
    while ( !workList.isEmpty() )
    {
      final WorkEntry workEntry = workList.pop();
      final Edge edge = workEntry.getEntry().getEdge();
      assert null != edge;
      final DependencyDescriptor dependency = edge.getDependency();
      final Coordinate coordinate = dependency.getCoordinate();
      final List<Binding> bindings = new ArrayList<>( graph.findAllBindingsByCoordinate( coordinate ) );

      if ( bindings.isEmpty() )
      {
        final String classname = coordinate.getType().toString();
        final InjectableDescriptor injectable = _registry.findInjectableByClassName( classname );
        if ( null != injectable && injectable.getBinding().getCoordinates().contains( coordinate ) )
        {
          bindings.add( injectable.getBinding() );
        }
        if ( bindings.isEmpty() )
        {
          final TypeElement typeElement = processingEnv.getElementUtils().getTypeElement( classname );
          final byte[] data = tryLoadDescriptorData( typeElement );
          if ( null != data )
          {
            try
            {
              final Object descriptor = loadDescriptor( classname, data );
              if ( descriptor instanceof InjectableDescriptor )
              {
                final InjectableDescriptor injectableDescriptor = (InjectableDescriptor) descriptor;
                _registry.registerInjectable( injectableDescriptor );
                bindings.add( injectableDescriptor.getBinding() );
              }
            }
            catch ( final IOException e )
            {
              final Node node = edge.getNode();
              final Object owner = node.hasNoBinding() ? null : node.getBinding().getOwner();
              final TypeElement ownerElement =
                owner instanceof FragmentDescriptor ? ( (FragmentDescriptor) owner ).getElement() :
                owner instanceof InjectableDescriptor ? ( (InjectableDescriptor) owner ).getElement() :
                injector.getElement();
              throw new ProcessorException( "Failed to read the Sting descriptor for " +
                                            "type " + classname + ". Error: " + e,
                                            ownerElement );
            }
          }
        }
      }

      final List<Binding> nullableProviders = bindings.stream()
        .filter( b -> Binding.Type.NULLABLE_PROVIDES == b.getBindingType() )
        .collect( Collectors.toList() );
      if ( !dependency.isOptional() && !nullableProviders.isEmpty() )
      {
        throw new ProcessorException( "Injector defined by type '" + injector.getElement().getQualifiedName() +
                                      "' contains a nullable provides method and a non-optional dependency " +
                                      coordinate + " with the same coordinate.\n" +
                                      "Dependency Path:\n" + workEntry.describePathFromRoot() + "\n" +
                                      "Binding" + ( nullableProviders.size() > 1 ? "s" : "" ) + ":\n" +
                                      bindingsToString( nullableProviders ),
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
          throw new ProcessorException( "Injector defined by type '" + injector.getElement().getQualifiedName() +
                                        "' is unable to satisfy non-optional dependency " + coordinate + ".\n" +
                                        "Path:\n" + workEntry.describePathFromRoot(),
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
              addDependsOnToWorkList( workList, node, workEntry );
            }
          }
          edge.setSatisfiedBy( nodes );
        }
        else
        {
          //noinspection ConstantConditions
          assert bindings.size() > 1 && !type.isCollection();
          throw new ProcessorException( "Injector defined by type '" + injector.getElement().getQualifiedName() +
                                        "' contains a dependency " + coordinate + " that expects to be satisfied " +
                                        "by a single value but the injector contains multiple values that satisfy " +
                                        "the dependency.\n\n" +
                                        "Dependency Path:\n" + workEntry.describePathFromRoot() + "\n" +
                                        "Bindings:\n" + bindingsToString( bindings ),
                                        dependency.getElement() );
        }
      }
    }
  }

  @Nonnull
  private String bindingsToString( @Nonnull final List<Binding> bindings )
  {
    return bindings
      .stream()
      .map( b -> "  " + b.getTypeLabel() + "    " + b.describe() )
      .collect( Collectors.joining( "\n" ) );
  }

  private void addDependsOnToWorkList( @Nonnull final Stack<WorkEntry> workList,
                                       @Nonnull final Node node,
                                       @Nullable final WorkEntry parent )
  {
    for ( final Edge e : node.getDependsOn() )
    {
      final Stack<PathEntry> stack = new Stack<>();
      if ( null != parent )
      {
        stack.addAll( parent.getStack() );
      }
      final PathEntry entry = new PathEntry( node, e );
      stack.add( entry );
      workList.add( new WorkEntry( entry, stack ) );
    }
  }

  private void emitObjectGraphJsonDescriptor( @Nonnull final ObjectGraph graph )
    throws IOException
  {
    if ( _emitJsonDescriptors )
    {
      final TypeElement element = graph.getInjector().getElement();
      final String filename = toFilename( element ) + GRAPH_SUFFIX;
      JsonUtil.writeJsonResource( processingEnv, element, filename, graph::write );
    }
  }

  private boolean isInjectorResolved( @Nonnull final InjectorDescriptor injector )
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
        final String classname = element.getQualifiedName().toString();
        if ( null == _registry.findFragmentByClassName( classname ) &&
             null == _registry.findInjectableByClassName( classname ) )
        {
          final byte[] data = tryLoadDescriptorData( element );
          if ( null == data )
          {
            return false;
          }
          try
          {
            final Object descriptor = loadDescriptor( classname, data );
            if ( descriptor instanceof FragmentDescriptor )
            {
              _registry.registerFragment( (FragmentDescriptor) descriptor );
            }
            else
            {
              _registry.registerInjectable( (InjectableDescriptor) descriptor );
            }
          }
          catch ( final IOException e )
          {
            throw new ProcessorException( "Failed to read the Sting descriptor for " +
                                          "include: " + classname + ". " +
                                          "Error: " + e,
                                          injector.getElement() );
          }
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
    emitInjectorJsonDescriptor( injector );
  }

  private void emitInjectorJsonDescriptor( @Nonnull final InjectorDescriptor injector )
    throws IOException
  {
    if ( _emitJsonDescriptors )
    {
      final TypeElement element = injector.getElement();
      final String filename = toFilename( element ) + JSON_SUFFIX;
      JsonUtil.writeJsonResource( processingEnv, element, filename, injector::write );
    }
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

    final boolean isDeclaredType = TypeKind.DECLARED == returnType.getKind();
    final DeclaredType declaredType = isDeclaredType ? (DeclaredType) returnType : null;
    final boolean isParameterizedType = isDeclaredType && !declaredType.getTypeArguments().isEmpty();
    final DependencyDescriptor.Type type;
    final TypeMirror dependencyValueType;
    if ( TypeKind.ARRAY == returnType.getKind() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.DEPENDENCY_CLASSNAME,
                                                          "return a value that is an array type" ),
                                    method );
    }
    else if ( null == declaredType )
    {
      type = DependencyDescriptor.Type.INSTANCE;
      dependencyValueType = returnType;
    }
    else if ( !isParameterizedType )
    {
      if ( Supplier.class.getCanonicalName().equals( getClassname( declaredType ) ) )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.DEPENDENCY_CLASSNAME,
                                                            "return a value that is a raw " +
                                                            Supplier.class.getCanonicalName() + " type" ),
                                      method );
      }
      else if ( Collection.class.getCanonicalName().equals( getClassname( declaredType ) ) )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.DEPENDENCY_CLASSNAME,
                                                            "return a value that is a raw " +
                                                            Collection.class.getCanonicalName() + " type" ),
                                      method );
      }
      else if ( !( (TypeElement) declaredType.asElement() ).getTypeParameters().isEmpty() )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.DEPENDENCY_CLASSNAME,
                                                            "return a value that is a raw parameterized " +
                                                            "type. Parameterized types are only permitted for " +
                                                            "specific types such as " +
                                                            Supplier.class.getCanonicalName() + " and " +
                                                            Collection.class.getCanonicalName() ),
                                      method );
      }
      type = DependencyDescriptor.Type.INSTANCE;
      dependencyValueType = returnType;
    }
    else
    {
      if ( Supplier.class.getCanonicalName().equals( getClassname( declaredType ) ) )
      {
        final TypeMirror typeArgument = declaredType.getTypeArguments().get( 0 );
        if ( TypeKind.WILDCARD == typeArgument.getKind() )
        {
          throw new ProcessorException( MemberChecks.mustNot( Constants.DEPENDENCY_CLASSNAME,
                                                              "return a value that is a " +
                                                              Supplier.class.getCanonicalName() +
                                                              " type with a wildcard parameter" ),
                                        method );
        }
        type = DependencyDescriptor.Type.SUPPLIER;
        dependencyValueType = typeArgument;
      }
      else if ( Collection.class.getCanonicalName().equals( getClassname( declaredType ) ) )
      {
        final TypeMirror typeArgument = declaredType.getTypeArguments().get( 0 );
        if ( TypeKind.WILDCARD == typeArgument.getKind() )
        {
          throw new ProcessorException( MemberChecks.mustNot( Constants.DEPENDENCY_CLASSNAME,
                                                              "return a value that is a " +
                                                              Collection.class.getCanonicalName() +
                                                              " type with a wildcard parameter" ),
                                        method );
        }
        else if ( TypeKind.DECLARED == typeArgument.getKind() &&
                  Supplier.class.getCanonicalName().equals( getClassname( (DeclaredType) typeArgument ) ) )
        {
          throw new IllegalStateException( "Not yet implemented" );
        }
        else
        {
          type = DependencyDescriptor.Type.COLLECTION;
          dependencyValueType = typeArgument;
        }
      }
      else
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.DEPENDENCY_CLASSNAME,
                                                            "return a value that is a parameterized type. " +
                                                            "This is only permitted for specific types such as " +
                                                            Supplier.class.getCanonicalName() + " and " +
                                                            Collection.class.getCanonicalName() ),
                                      method );
      }
    }

    final Coordinate coordinate = new Coordinate( qualifier, dependencyValueType );
    return new DependencyDescriptor( type, coordinate, optional, method, -1 );
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
    writeBinaryDescriptor( element, fragment );
    emitFragmentJsonDescriptor( fragment );
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
           !AnnotationsUtil.hasAnnotationOfType( includeElement, Constants.INJECTABLE_CLASSNAME ) )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( annotationClassname ) + " target has an " +
                                      "includes parameter containing the value " + include + " that is not a type " +
                                      "annotated by either " +
                                      MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME ) + " or " +
                                      MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME ),
                                      element );
      }
      else
      {
        results.add( (DeclaredType) include );
      }
    }
    return results;
  }

  private void emitFragmentJsonDescriptor( @Nonnull final FragmentDescriptor fragment )
    throws IOException
  {
    if ( _emitJsonDescriptors )
    {
      final TypeElement element = fragment.getElement();
      final String filename = toFilename( element ) + JSON_SUFFIX;
      JsonUtil.writeJsonResource( processingEnv, element, filename, fragment::write );
    }
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
        dependencies.add( processFragmentDependencyParameter( parameter, parameterTypes.get( index ), index ) );
        index++;
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
                                                                   @Nonnull final TypeMirror parameterType,
                                                                   final int parameterIndex )
  {
    final boolean optional =
      AnnotationsUtil.hasAnnotationOfType( parameter, GeneratorUtil.NULLABLE_ANNOTATION_CLASSNAME );
    final AnnotationMirror annotation =
      AnnotationsUtil.findAnnotationByType( parameter, Constants.DEPENDENCY_CLASSNAME );
    final String qualifier =
      null == annotation ? "" : AnnotationsUtil.getAnnotationValue( annotation, "qualifier" );

    final boolean isDeclaredType = TypeKind.DECLARED == parameterType.getKind();
    final DeclaredType declaredType = isDeclaredType ? (DeclaredType) parameterType : null;
    final boolean isParameterizedType = isDeclaredType && !declaredType.getTypeArguments().isEmpty();
    final DependencyDescriptor.Type type;
    final TypeMirror dependencyValueType;
    if ( TypeKind.ARRAY == parameterType.getKind() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                          "have a method with a parameter that is an array type" ),
                                    parameter );
    }
    else if ( null == declaredType )
    {
      type = DependencyDescriptor.Type.INSTANCE;
      dependencyValueType = parameterType;
    }
    else if ( !isParameterizedType )
    {
      if ( Supplier.class.getCanonicalName().equals( getClassname( declaredType ) ) )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                            "have a method with a parameter that is a raw " +
                                                            Supplier.class.getCanonicalName() + " type" ),
                                      parameter );
      }
      else if ( Collection.class.getCanonicalName().equals( getClassname( declaredType ) ) )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                            "have a method with a parameter that is a raw " +
                                                            Collection.class.getCanonicalName() + " type" ),
                                      parameter );
      }
      else if ( !( (TypeElement) declaredType.asElement() ).getTypeParameters().isEmpty() )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                            "have a method with a parameter that is a " +
                                                            "raw parameterized type. Parameterized types are only " +
                                                            "permitted for specific types such as " +
                                                            Supplier.class.getCanonicalName() + " and " +
                                                            Collection.class.getCanonicalName() ),
                                      parameter );
      }
      type = DependencyDescriptor.Type.INSTANCE;
      dependencyValueType = parameterType;
    }
    else
    {
      if ( Supplier.class.getCanonicalName().equals( getClassname( declaredType ) ) )
      {
        final TypeMirror typeArgument = declaredType.getTypeArguments().get( 0 );
        if ( TypeKind.WILDCARD == typeArgument.getKind() )
        {
          throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                              "have a method with a parameter that is a " +
                                                              Supplier.class.getCanonicalName() +
                                                              " type with a wildcard parameter" ),
                                        parameter );
        }
        type = DependencyDescriptor.Type.SUPPLIER;
        dependencyValueType = typeArgument;
      }
      else if ( Collection.class.getCanonicalName().equals( getClassname( declaredType ) ) )
      {
        final TypeMirror typeArgument = declaredType.getTypeArguments().get( 0 );
        if ( TypeKind.WILDCARD == typeArgument.getKind() )
        {
          throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                              "have a method with a parameter that is a " +
                                                              Collection.class.getCanonicalName() +
                                                              " type with a wildcard parameter" ),
                                        parameter );
        }
        else if ( TypeKind.DECLARED == typeArgument.getKind() &&
                  Supplier.class.getCanonicalName().equals( getClassname( (DeclaredType) typeArgument ) ) )
        {
          throw new IllegalStateException( "Not yet implemented" );
        }
        else
        {
          type = DependencyDescriptor.Type.COLLECTION;
          dependencyValueType = typeArgument;
        }
      }
      else
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                            "have a method with a parameter that is a " +
                                                            "parameterized type. This is only permitted for " +
                                                            "specific types such as " +
                                                            Supplier.class.getCanonicalName() + " and " +
                                                            Collection.class.getCanonicalName() ),
                                      parameter );
      }
    }

    final Coordinate coordinate = new Coordinate( qualifier, dependencyValueType );
    return new DependencyDescriptor( type, coordinate, optional, parameter, parameterIndex );
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
      dependencies.add( handleConstructorParameter( parameter, parameterTypes.get( index ), index ) );
      index++;
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

    writeBinaryDescriptor( element, injectable );
    emitInjectableJsonDescriptor( injectable );
  }

  private void writeBinaryDescriptor( @Nonnull final TypeElement element,
                                      @Nonnull final Object descriptor )
    throws IOException
  {
    final String[] nameParts = extractNameParts( element );

    // Write out the descriptor
    final FileObject resource =
      processingEnv.getFiler().createResource( StandardLocation.CLASS_OUTPUT, nameParts[ 0 ], nameParts[ 1 ], element );
    try ( final OutputStream out = resource.openOutputStream() )
    {
      try ( final DataOutputStream dos1 = new DataOutputStream( out ) )
      {
        _descriptorIO.write( dos1, descriptor );
      }
    }

    if ( _verifyDescriptors )
    {
      verifyDescriptor( element, descriptor );
    }
  }

  @Nonnull
  private String[] extractNameParts( @Nonnull final TypeElement element )
  {
    final String binaryName = processingEnv.getElementUtils().getBinaryName( element ).toString();
    final int lastIndex = binaryName.lastIndexOf( "." );
    final String packageName = -1 == lastIndex ? "" : binaryName.substring( 0, lastIndex );
    final String relativeName = binaryName.substring( -1 == lastIndex ? 0 : lastIndex + 1 ) + SUFFIX;

    return new String[]{ packageName, relativeName };
  }

  private void verifyDescriptor( @Nonnull final TypeElement element, @Nonnull final Object descriptor )
    throws IOException
  {
    final ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
    try ( final DataOutputStream dos = new DataOutputStream( baos1 ) )
    {
      _descriptorIO.write( dos, descriptor );
    }
    final Object newDescriptor;
    try ( final DataInputStream dos = new DataInputStream( new ByteArrayInputStream( baos1.toByteArray() ) ) )
    {
      newDescriptor = _descriptorIO.read( dos, element.getQualifiedName().toString() );
    }
    final ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
    try ( final DataOutputStream dos = new DataOutputStream( baos2 ) )
    {
      _descriptorIO.write( dos, newDescriptor );
    }

    if ( !Arrays.equals( baos1.toByteArray(), baos2.toByteArray() ) )
    {
      throw new ProcessorException( "Failed to emit valid binary descriptor for " + element.getQualifiedName() +
                                    ". Reading the emitted descriptor did not produce the same value.",
                                    element );
    }
  }

  private void emitInjectableJsonDescriptor( @Nonnull final InjectableDescriptor injectable )
    throws IOException
  {
    if ( _emitJsonDescriptors )
    {
      final TypeElement element = injectable.getElement();
      final String filename = toFilename( element ) + JSON_SUFFIX;
      JsonUtil.writeJsonResource( processingEnv, element, filename, injectable::write );
    }
  }

  @Nonnull
  private String toFilename( @Nonnull final TypeElement typeElement )
  {
    return GeneratorUtil.getGeneratedClassName( typeElement, "", "" ).toString().replace( ".", "/" );
  }

  @Nonnull
  private DependencyDescriptor handleConstructorParameter( @Nonnull final VariableElement parameter,
                                                           @Nonnull final TypeMirror parameterType,
                                                           final int parameterIndex )
  {
    final boolean optional =
      AnnotationsUtil.hasAnnotationOfType( parameter, GeneratorUtil.NULLABLE_ANNOTATION_CLASSNAME );
    final AnnotationMirror annotation =
      AnnotationsUtil.findAnnotationByType( parameter, Constants.DEPENDENCY_CLASSNAME );
    final String qualifier =
      null == annotation ? "" : AnnotationsUtil.getAnnotationValue( annotation, "qualifier" );

    final boolean isDeclaredType = TypeKind.DECLARED == parameterType.getKind();
    final DeclaredType declaredType = isDeclaredType ? (DeclaredType) parameterType : null;
    final boolean isParameterizedType = isDeclaredType && !declaredType.getTypeArguments().isEmpty();
    final DependencyDescriptor.Type type;
    final TypeMirror dependencyValueType;
    if ( TypeKind.ARRAY == parameterType.getKind() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                          "have a constructor with a parameter that is an array type" ),
                                    parameter );
    }
    else if ( null == declaredType )
    {
      type = DependencyDescriptor.Type.INSTANCE;
      dependencyValueType = parameterType;
    }
    else if ( !isParameterizedType )
    {
      if ( Supplier.class.getCanonicalName().equals( getClassname( declaredType ) ) )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                            "have a constructor with a parameter that is a " +
                                                            "raw " + Supplier.class.getCanonicalName() + " type" ),
                                      parameter );
      }
      else if ( Collection.class.getCanonicalName().equals( getClassname( declaredType ) ) )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                            "have a constructor with a parameter that is a " +
                                                            "raw " + Collection.class.getCanonicalName() + " type" ),
                                      parameter );
      }
      else if ( !( (TypeElement) declaredType.asElement() ).getTypeParameters().isEmpty() )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                            "have a constructor with a parameter that is a " +
                                                            "raw parameterized type. Parameterized types are only " +
                                                            "permitted for specific types such as " +
                                                            Supplier.class.getCanonicalName() + " and " +
                                                            Collection.class.getCanonicalName() ),
                                      parameter );
      }
      type = DependencyDescriptor.Type.INSTANCE;
      dependencyValueType = parameterType;
    }
    else
    {
      if ( Supplier.class.getCanonicalName().equals( getClassname( declaredType ) ) )
      {
        final TypeMirror typeArgument = declaredType.getTypeArguments().get( 0 );
        if ( TypeKind.WILDCARD == typeArgument.getKind() )
        {
          throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                              "have a constructor with a parameter that is " +
                                                              "a " + Supplier.class.getCanonicalName() +
                                                              " type with a wildcard parameter" ),
                                        parameter );
        }
        type = DependencyDescriptor.Type.SUPPLIER;
        dependencyValueType = typeArgument;
      }
      else if ( Collection.class.getCanonicalName().equals( getClassname( declaredType ) ) )
      {
        final TypeMirror typeArgument = declaredType.getTypeArguments().get( 0 );
        if ( TypeKind.WILDCARD == typeArgument.getKind() )
        {
          throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                              "have a constructor with a parameter that " +
                                                              "is a " + Collection.class.getCanonicalName() +
                                                              " type with a wildcard parameter" ),
                                        parameter );
        }
        else if ( TypeKind.DECLARED == typeArgument.getKind() &&
                  Supplier.class.getCanonicalName().equals( getClassname( (DeclaredType) typeArgument ) ) )
        {
          throw new IllegalStateException( "Not yet implemented" );
        }
        else
        {
          type = DependencyDescriptor.Type.COLLECTION;
          dependencyValueType = typeArgument;
        }
      }
      else
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                            "have a constructor with a parameter that is " +
                                                            "a parameterized type. This is only permitted for " +
                                                            "specific types such as " +
                                                            Supplier.class.getCanonicalName() + " and " +
                                                            Collection.class.getCanonicalName() ),
                                      parameter );
      }
    }

    final Coordinate coordinate = new Coordinate( qualifier, dependencyValueType );
    return new DependencyDescriptor( type, coordinate, optional, parameter, parameterIndex );
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

  @Nonnull
  private String getClassname( @Nonnull final DeclaredType declaredType )
  {
    return ( (TypeElement) declaredType.asElement() ).getQualifiedName().toString();
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

  @Nonnull
  private Object loadDescriptor( final String classname, final byte[] data )
    throws IOException
  {
    return _descriptorIO.read( new DataInputStream( new ByteArrayInputStream( data ) ), classname );
  }

  @Nullable
  private byte[] tryLoadDescriptorData( @Nonnull final TypeElement element )
  {
    final byte[] data = tryLoadDescriptorData( StandardLocation.CLASS_PATH, element );
    return null != data ? data : tryLoadDescriptorData( StandardLocation.CLASS_OUTPUT, element );
  }

  @Nullable
  private byte[] tryLoadDescriptorData( @Nonnull final JavaFileManager.Location location,
                                        @Nonnull final TypeElement element )
  {
    final String[] nameParts = extractNameParts( element );
    try
    {
      return IOUtil.readFully( processingEnv.getFiler().getResource( location, nameParts[ 0 ], nameParts[ 1 ] ) );
    }
    catch ( final IOException ignored )
    {
      return null;
    }
    catch ( final RuntimeException e )
    {
      // The javac compiler in Java8 will return a null from the JavaFileManager when it should
      // throw an IOException which later causes a NullPointerException when wrapping the code
      // This ugly hack works around this scenario and just lets the compile continue
      if ( e.getClass().getCanonicalName().equals( "com.sun.tools.javac.util.ClientCodeException" ) &&
           e.getCause() instanceof NullPointerException )
      {
        return null;
      }
      else
      {
        throw e;
      }
    }
  }
}
