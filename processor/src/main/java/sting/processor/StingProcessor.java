package sting.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
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
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import org.realityforge.proton.AbstractStandardProcessor;
import org.realityforge.proton.AnnotationsUtil;
import org.realityforge.proton.DeferredElementSet;
import org.realityforge.proton.ElementsUtil;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.JsonUtil;
import org.realityforge.proton.MemberChecks;
import org.realityforge.proton.ProcessorException;
import org.realityforge.proton.ResourceUtil;
import org.realityforge.proton.StopWatch;
import org.realityforge.proton.SuperficialValidation;
import org.realityforge.proton.TypesUtil;

/**
 * The annotation processor that analyzes Sting annotated source code and generates an injector and supporting artifacts.
 */
@SuppressWarnings( "DuplicatedCode" )
@SupportedAnnotationTypes( { Constants.INJECTOR_CLASSNAME,
                             Constants.INJECTOR_FRAGMENT_CLASSNAME,
                             Constants.FACTORY_CLASSNAME,
                             Constants.INJECTABLE_CLASSNAME,
                             Constants.FRAGMENT_CLASSNAME,
                             Constants.EAGER_CLASSNAME,
                             Constants.TYPED_CLASSNAME,
                             Constants.NAMED_CLASSNAME } )
@SupportedSourceVersion( SourceVersion.RELEASE_17 )
@SupportedOptions( { "sting.defer.unresolved",
                     "sting.defer.errors",
                     "sting.warnings_as_errors",
                     "sting.debug",
                     "sting.profile",
                     "sting.emit_json_descriptors",
                     "sting.emit_dot_reports",
                     "sting.verbose_out_of_round.errors" } )
public final class StingProcessor
  extends AbstractStandardProcessor
{
  /**
   * Extension for json descriptors.
   */
  static final String JSON_SUFFIX = ".sting.json";
  /**
   * Extension for graphviz .dot reports.
   */
  static final String DOT_SUFFIX = ".gv";
  // Binary descriptor persistence removed
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
  @Nonnull
  private final DeferredElementSet _deferredInjectableTypes = new DeferredElementSet();
  @Nonnull
  private final DeferredElementSet _deferredFragmentTypes = new DeferredElementSet();
  @Nonnull
  private final DeferredElementSet _deferredFactoryTypes = new DeferredElementSet();
  @Nonnull
  private final DeferredElementSet _deferredInjectorTypes = new DeferredElementSet();
  @Nonnull
  private final DeferredElementSet _deferredInjectorFragmentTypes = new DeferredElementSet();
  @Nonnull
  private final StopWatch _analyzeInjectableStopWatch = new StopWatch( "Analyze Injectable" );
  @Nonnull
  private final StopWatch _analyzeFragmentStopWatch = new StopWatch( "Analyze Fragment" );
  @Nonnull
  private final StopWatch _analyzeFactoryStopWatch = new StopWatch( "Analyze Factory" );
  @Nonnull
  private final StopWatch _analyzeInjectorFragmentStopWatch = new StopWatch( "Analyze Injector Fragment" );
  @Nonnull
  private final StopWatch _analyzeInjectorStopWatch = new StopWatch( "Analyze Injector" );
  private final StopWatch _generateInjectableStubStopWatch = new StopWatch( "Generate Injectable Stub" );
  @Nonnull
  private final StopWatch _generateFragmentStubStopWatch = new StopWatch( "Generate Fragment Stub" );
  @Nonnull
  private final StopWatch _generateFactoryImplStopWatch = new StopWatch( "Generate Factory Impl" );
  @Nonnull
  private final StopWatch _generateInjectorImplStopWatch = new StopWatch( "Generate Injector Impl" );
  @Nonnull
  private final StopWatch _isInjectorResolvedStopWatch = new StopWatch( "Is Injector Resolved" );
  @Nonnull
  private final StopWatch _buildAndEmitObjectGraphStopWatch = new StopWatch( "Build and Emit Object Graph" );
  /**
   * Flag controlling whether json descriptors are emitted.
   * Json descriptors are primarily used during debugging and probably should not be enabled in production code.
   */
  private boolean _emitJsonDescriptors;
  /**
   * Flag controlling whether .dot formatted report is emitted.
   * The .dot report is typically used by end users who want to explore the graph.
   */
  private boolean _emitDotReports;
  /**
   * Cache of derived fragment descriptors during a processing session.
   */
  @Nonnull
  private final Map<String, FragmentDescriptor> _derivedFragmentCache = new HashMap<>();
  /**
   * Cache of derived injectable descriptors during a processing session.
   */
  @Nonnull
  private final Map<String, InjectableDescriptor> _derivedInjectableCache = new HashMap<>();
  /**
   * Track fragments that are currently being resolved to break include cycles.
   */
  @Nonnull
  private final Set<String> _resolvingFragmentTypes = new HashSet<>();

  @Nonnull
  @Override
  protected String getIssueTrackerURL()
  {
    return "https://github.com/sting-ioc/sting/issues";
  }

  @Nonnull
  @Override
  protected String getOptionPrefix()
  {
    return "sting";
  }

  @Override
  public synchronized void init( @Nonnull final ProcessingEnvironment processingEnv )
  {
    super.init( processingEnv );
    _emitJsonDescriptors = readBooleanOption( "emit_json_descriptors", false );
    _emitDotReports = readBooleanOption( "emit_dot_reports", false );
  }

  private void warning( @Nonnull final CharSequence message, @Nonnull final Element element )
  {
    processingEnv.getMessager().printMessage( warningKind(), message, element );
  }

  @Nonnull
  private Diagnostic.Kind warningKind()
  {
    return warningsAsErrors() ? Diagnostic.Kind.ERROR : Diagnostic.Kind.WARNING;
  }

  private boolean warningsAsErrors()
  {
    return "true".equalsIgnoreCase( processingEnv.getOptions().get( "sting.warnings_as_errors" ) );
  }

  @Override
  protected void collectStopWatches( @Nonnull final Collection<StopWatch> stopWatches )
  {
    stopWatches.add( _analyzeInjectableStopWatch );
    stopWatches.add( _analyzeFragmentStopWatch );
    stopWatches.add( _analyzeFactoryStopWatch );
    stopWatches.add( _analyzeInjectorFragmentStopWatch );
    stopWatches.add( _analyzeInjectorStopWatch );
    stopWatches.add( _generateInjectableStubStopWatch );
    stopWatches.add( _generateFragmentStubStopWatch );
    stopWatches.add( _generateFactoryImplStopWatch );
    stopWatches.add( _generateInjectorImplStopWatch );
    stopWatches.add( _isInjectorResolvedStopWatch );
    stopWatches.add( _buildAndEmitObjectGraphStopWatch );
  }

  @Override
  public boolean process( @Nonnull final Set<? extends TypeElement> annotations, @Nonnull final RoundEnvironment env )
  {
    debugAnnotationProcessingRootElements( env );
    collectRootTypeNames( env );

    processTypeElements( annotations,
                         env,
                         Constants.FACTORY_CLASSNAME,
                         _deferredFactoryTypes,
                         "Analyze Factory",
                         this::processFactory,
                         _analyzeFactoryStopWatch );

    processTypeElements( annotations,
                         env,
                         Constants.INJECTABLE_CLASSNAME,
                         _deferredInjectableTypes,
                         "Analyze Injectable",
                         this::processInjectable,
                         _analyzeInjectableStopWatch );

    processTypeElements( annotations,
                         env,
                         Constants.FRAGMENT_CLASSNAME,
                         _deferredFragmentTypes,
                         "Analyze Fragment",
                         this::processFragment,
                         _analyzeFragmentStopWatch );

    annotations
      .stream()
      .filter( a -> a.getQualifiedName().toString().equals( Constants.NAMED_CLASSNAME ) )
      .findAny()
      .ifPresent( a -> verifyNamedElements( env, env.getElementsAnnotatedWith( a ) ) );

    annotations
      .stream()
      .filter( a -> a.getQualifiedName().toString().equals( Constants.TYPED_CLASSNAME ) )
      .findAny()
      .ifPresent( a -> verifyTypedElements( env, env.getElementsAnnotatedWith( a ) ) );

    annotations.stream()
      .filter( a -> a.getQualifiedName().toString().equals( Constants.EAGER_CLASSNAME ) )
      .findAny()
      .ifPresent( a -> verifyEagerElements( env, env.getElementsAnnotatedWith( a ) ) );

    processTypeElements( annotations,
                         env,
                         Constants.INJECTOR_FRAGMENT_CLASSNAME,
                         _deferredInjectorFragmentTypes,
                         "Analyze Injector Fragment",
                         this::processInjectorFragment,
                         _analyzeInjectorFragmentStopWatch );

    processTypeElements( annotations,
                         env,
                         Constants.INJECTOR_CLASSNAME,
                         _deferredInjectorTypes,
                         "Analyze Injector",
                         this::processInjector,
                         _analyzeInjectorStopWatch );

    processResolvedFactories( env );
    processResolvedInjectables( env );
    processResolvedFragments( env );
    processResolvedInjectors( env );

    errorIfProcessingOverAndInvalidTypesDetected( env );
    errorIfProcessingOverAndUnprocessedInjectorDetected( env );
    if ( env.processingOver() || env.errorRaised() )
    {
      _registry.clear();
      _derivedFragmentCache.clear();
      _derivedInjectableCache.clear();
    }
    clearRootTypeNamesIfProcessingOver( env );
    reportProfilerTimings();
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
                         "as not all of their dependencies could be resolved. Ensure that included types are " +
                         "present on the classpath and are valid Sting components. If the problem is not " +
                         "obvious, consider passing the annotation option sting.debug=true" );
        for ( final InjectorDescriptor injector : injectors )
        {
          processingEnv
            .getMessager()
            .printMessage( Diagnostic.Kind.ERROR,
                           "Failed to process the " + injector.getElement().getQualifiedName() + " injector." );
        }
      }
    }
  }

  private void processResolvedInjectables( @Nonnull final RoundEnvironment env )
  {
    for ( final InjectableDescriptor injectable : new ArrayList<>( _registry.getInjectables() ) )
    {
      if ( !injectable.isJavaStubGenerated() )
      {
        performAction( env, "Generate Injectable Stub", e -> {
          injectable.markJavaStubAsGenerated();
          emitInjectableJsonDescriptor( injectable );
          emitInjectableStub( injectable );
        }, injectable.getElement(), _generateInjectableStubStopWatch );
      }
    }
  }

  private void processResolvedFactories( @Nonnull final RoundEnvironment env )
  {
    for ( final FactoryDescriptor factory : new ArrayList<>( _registry.getFactories() ) )
    {
      if ( !factory.isGenerated() )
      {
        performAction( env, "Generate Factory Impl", e -> {
          factory.markGenerated();
          emitFactoryImpl( factory );
        }, factory.getElement(), _generateFactoryImplStopWatch );
      }
    }
  }

  private void emitFactoryImpl( @Nonnull final FactoryDescriptor factory )
    throws IOException
  {
    debug( () -> "Emitting factory implementation for the factory " + factory.getElement().getQualifiedName() );
    final String packageName = GeneratorUtil.getQualifiedPackageName( factory.getElement() );
    emitTypeSpec( packageName, FactoryGenerator.buildType( processingEnv, factory ) );
  }

  private void emitInjectableStub( @Nonnull final InjectableDescriptor injectable )
    throws IOException
  {
    debug( () -> "Emitting injectable stub for the injectable " + injectable.getElement().getQualifiedName() );
    final String packageName = GeneratorUtil.getQualifiedPackageName( injectable.getElement() );
    emitTypeSpec( packageName, InjectableGenerator.buildType( processingEnv, injectable ) );
  }

  private void processResolvedFragments( @Nonnull final RoundEnvironment env )
  {
    final List<FragmentDescriptor> deferred = new ArrayList<>();
    final List<FragmentDescriptor> current = new ArrayList<>( _registry.getFragments() );
    final AtomicBoolean resolvedType = new AtomicBoolean();

    while ( !current.isEmpty() )
    {
      for ( final FragmentDescriptor fragment : current )
      {
        if ( !fragment.isJavaStubGenerated() && !fragment.containsError() )
        {
          performAction( env, "Generate Fragment Stub", e -> {
            final ResolveType resolveType = isFragmentResolved( env, fragment );
            if ( ResolveType.RESOLVED == resolveType )
            {
              fragment.markJavaStubAsGenerated();
              emitFragmentJsonDescriptor( fragment );
              emitFragmentStub( fragment );
            }
            else if ( ResolveType.MAYBE_UNRESOLVED == resolveType )
            {
              debug( () -> "The fragment " + fragment.getElement().getQualifiedName() +
                           " has resolved java types but unresolved descriptors. Deferring processing " +
                           "until later in the round" );
              deferred.add( fragment );
            }
            else
            {
              debug( () -> "Defer generation for the fragment " + fragment.getElement().getQualifiedName() +
                           " as it is not yet resolved" );
            }
          }, fragment.getElement(), _generateFragmentStubStopWatch );
        }
      }
      current.clear();
      if ( resolvedType.get() )
      {
        current.addAll( deferred );
        deferred.clear();
      }
      else
      {
        break;
      }
    }
  }

  @Nonnull
  private ResolveType isFragmentReady( @Nonnull final RoundEnvironment env,
                                       @Nonnull final FragmentDescriptor fragment )
  {
    if ( fragment.containsError() )
    {
      return ResolveType.UNRESOLVED;
    }
    else
    {
      return isFragmentResolved( env, fragment );
    }
  }

  private void emitFragmentStub( @Nonnull final FragmentDescriptor fragment )
    throws IOException
  {
    debug( () -> "Emitting fragment stub for the fragment " + fragment.getElement().getQualifiedName() );
    final String packageName = GeneratorUtil.getQualifiedPackageName( fragment.getElement() );
    emitTypeSpec( packageName, FragmentGenerator.buildType( processingEnv, fragment ) );
  }

  private void processResolvedInjectors( @Nonnull final RoundEnvironment env )
  {
    final boolean profileEnabled = isProfileEnabled();

    final List<InjectorDescriptor> deferred = new ArrayList<>();
    final List<InjectorDescriptor> current = new ArrayList<>( _registry.getInjectors() );
    final AtomicBoolean resolvedType = new AtomicBoolean();

    while ( !current.isEmpty() )
    {
      for ( final InjectorDescriptor injector : current )
      {
        if ( !injector.containsError() )
        {
          performAction( env, "Generate Injector Impl", e -> {
            if ( profileEnabled )
            {
              _isInjectorResolvedStopWatch.start();
            }
            final ResolveType resolveType = isInjectorResolved( env, injector );
            if ( profileEnabled )
            {
              _isInjectorResolvedStopWatch.stop();
            }
            if ( ResolveType.RESOLVED == resolveType )
            {
              _registry.deregisterInjector( injector );
              if ( profileEnabled )
              {
                _buildAndEmitObjectGraphStopWatch.start();
              }
              try
              {
                buildAndEmitObjectGraph( injector );
              }
              finally
              {
                if ( profileEnabled )
                {
                  _buildAndEmitObjectGraphStopWatch.stop();
                }
              }
              resolvedType.set( true );
            }
            else if ( ResolveType.MAYBE_UNRESOLVED == resolveType )
            {
              debug( () -> "The injector " + injector.getElement().getQualifiedName() +
                           " has resolved java types but unresolved descriptors. Deferring processing " +
                           "until later in the round" );
              deferred.add( injector );
            }
            else
            {
              debug( () -> "Defer generation for the injector " + injector.getElement().getQualifiedName() +
                           " as it is not yet resolved" );
            }
          }, injector.getElement(), _generateInjectorImplStopWatch );
        }
      }
      current.clear();
      if ( resolvedType.get() )
      {
        resolvedType.set( false );
        current.addAll( deferred );
        deferred.clear();
      }
      else
      {
        break;
      }
    }
  }

  private void buildAndEmitObjectGraph( @Nonnull final InjectorDescriptor injector )
    throws Exception
  {
    debug( () -> "Preparing to build component graph for the injector " + injector.getElement().getQualifiedName() );
    final ComponentGraph graph = new ComponentGraph( injector );
    registerIncludesComponents( graph );

    registerInputs( graph );

    debug( () -> "Building component graph for the injector " + injector.getElement().getQualifiedName() );

    buildObjectGraphNodes( graph );

    if ( graph.getNodes().isEmpty() && graph.getRootNode().getDependsOn().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.toSimpleName( Constants.INJECTOR_CLASSNAME ) + " target " +
                                    "produced an empty object graph. This means that there are no eager nodes " +
                                    "in the includes and there are no dependencies or only unsatisfied optional " +
                                    "dependencies defined by the injector",
                                    graph.getInjector().getElement() );
    }

    debug( () -> "Propagating eager-ness for the injector " + injector.getElement().getQualifiedName() );

    propagateEagerFlagUpstream( graph );

    debug( () -> "Verifying no circular dependencies for the injector " + injector.getElement().getQualifiedName() );

    CircularDependencyChecker.verifyNoCircularDependencyLoops( graph );

    final Set<Binding> actualBindings =
      graph.getNodes().stream().map( Node::getBinding ).collect( Collectors.toSet() );

    for ( final Map.Entry<IncludeDescriptor, Set<Binding>> entry : graph.getIncludeRootToBindingMap().entrySet() )
    {
      if ( entry.getValue().stream().noneMatch( actualBindings::contains ) )
      {
        final IncludeDescriptor includeRoot = entry.getKey();
        throw new ProcessorException( MemberChecks.toSimpleName( Constants.INJECTOR_CLASSNAME ) + " must not " +
                                      "include type " + includeRoot.getIncludedType() +
                                      " when the type is not used within the graph",
                                      graph.getInjector().getElement() );
      }
    }

    emitObjectGraphJsonDescriptor( graph );

    final String packageName = GeneratorUtil.getQualifiedPackageName( graph.getInjector().getElement() );

    debug( () -> "Emitting injector implementation for the injector " +
                 graph.getInjector().getElement().getQualifiedName() );
    emitTypeSpec( packageName, InjectorGenerator.buildType( processingEnv, graph ) );

    if ( injector.isInjectable() )
    {
      debug( () -> "Emitting injector provider for the injector " +
                   graph.getInjector().getElement().getQualifiedName() );
      emitTypeSpec( packageName, InjectorProviderGenerator.buildType( processingEnv, graph ) );
    }
    emitDotReport( graph );
  }

  private void emitDotReport( @Nonnull final ComponentGraph graph )
    throws IOException
  {
    if ( _emitDotReports )
    {
      final TypeElement element = graph.getInjector().getElement();
      final String filename = toFilename( element ) + DOT_SUFFIX;
      debug( () -> "Emitting .dot report for the injector " + graph.getInjector().getElement().getQualifiedName() );

      final String report = InjectorDotReportGenerator.buildDotReport( processingEnv, graph );
      ResourceUtil.writeResource( processingEnv, filename, report, element );
    }
  }

  private void registerInputs( @Nonnull final ComponentGraph graph )
  {
    for ( final InputDescriptor input : graph.getInjector().getInputs() )
    {
      graph.registerInput( input );
    }
  }

  private void propagateEagerFlagUpstream( @Nonnull final ComponentGraph graph )
  {
    // Propagate Eager flag to all dependencies of eager nodes breaking the propagation at Supplier nodes
    // They may not be configured as eager but they are effectively eager given that they will be created
    // at startup, they may as well be marked as eager objects as that results in smaller code-size.
    graph.getNodes().stream().filter( n -> n.getBinding().isEager() ).forEach( Node::markNodeAndUpstreamAsEager );
  }

  private void registerIncludesComponents( @Nonnull final ComponentGraph graph )
  {
    registerIncludes( graph, null, graph.getInjector().getIncludes() );
  }

  private void registerIncludes( @Nonnull final ComponentGraph graph,
                                 @Nullable final IncludeDescriptor includeRoot,
                                 @Nonnull final Collection<IncludeDescriptor> includes )
  {
    for ( final IncludeDescriptor include : includes )
    {
      final String classname = include.getActualTypeName();
      if ( isDebugEnabled() && include.isProvider() )
      {
        debug( () -> "Registering include " + classname + " via provider " + include.getIncludedType() +
                     " into graph " + graph.getInjector().getElement().getQualifiedName() );
      }
      else
      {
        debug( () -> "Registering include " + classname + " into graph " +
                     graph.getInjector().getElement().getQualifiedName() );
      }
      final IncludeDescriptor root = null == includeRoot ? include : includeRoot;
      final TypeElement element = processingEnv.getElementUtils().getTypeElement( classname );
      assert null != element;
      final String qualifiedName = element.getQualifiedName().toString();
      if ( AnnotationsUtil.hasAnnotationOfType( element, Constants.FRAGMENT_CLASSNAME ) )
      {
        final FragmentDescriptor fragment = _registry.getFragmentByClassName( qualifiedName );
        registerIncludes( graph, root, fragment.getIncludes() );
        graph.registerFragment( root, fragment );
      }
      else
      {
        assert AnnotationsUtil.hasAnnotationOfType( element, Constants.INJECTABLE_CLASSNAME );
        final InjectableDescriptor injectable = _registry.getInjectableByClassName( qualifiedName );
        graph.registerInjectable( root, injectable );
      }
    }
  }

  private void buildObjectGraphNodes( @Nonnull final ComponentGraph graph )
  {
    final Set<Node> completed = new HashSet<>();
    final Stack<WorkEntry> workList = new Stack<>();
    // At this stage the "rootNode" contains dependencies for all the output methods declared on the injector
    // and all the eager services declared in includes have already been added to the nodes list.
    //
    // We start at the rootNode and expand all of the dependencies. And then we take any of the eager dependencies
    // that have yet to be added to be processed and add them to to worklist and expand all dependencies until there
    // are no eager nodes left to process
    final Node rootNode = graph.getRootNode();
    rootNode.setDepth( 0 );
    addDependsOnToWorkList( workList, rootNode, null );
    processWorkList( graph, completed, workList );
    List<Node> eagerNodes = graph.getRawNodeCollection().stream().filter( n -> n.getBinding().isEager() ).toList();
    while ( !eagerNodes.isEmpty() )
    {
      for ( final Node node : eagerNodes )
      {
        if ( node.isDepthNotSet() )
        {
          node.setDepth( 0 );
          addDependsOnToWorkList( workList, node, null );
          processWorkList( graph, completed, workList );
        }
      }
      eagerNodes =
        graph.getRawNodeCollection().stream().filter( n -> n.getBinding().isEager() && n.isDepthNotSet() ).toList();
    }
    graph.complete();
  }

  private void processWorkList( @Nonnull final ComponentGraph graph,
                                @Nonnull final Set<Node> completed,
                                @Nonnull final Stack<WorkEntry> workList )
  {
    while ( !workList.isEmpty() )
    {
      final WorkEntry workEntry = workList.pop();
      final Edge edge = workEntry.getEntry().getEdge();
      assert null != edge;
      final ServiceRequest serviceRequest = edge.getServiceRequest();
      final Coordinate coordinate = serviceRequest.getService().getCoordinate();
      final List<Binding> bindings = new ArrayList<>( graph.findAllBindingsByCoordinate( coordinate ) );

      if ( bindings.isEmpty() && coordinate.getQualifier().isEmpty() )
      {
        final String typename = coordinate.getType().toString();
        final InjectableDescriptor injectable = _registry.findInjectableByClassName( typename );
        if ( null != injectable && injectable.isAutoDiscoverable() )
        {
          bindings.add( injectable.getBinding() );
        }
        if ( bindings.isEmpty() )
        {
          final TypeElement typeElement = processingEnv.getElementUtils().getTypeElement( typename );
          if ( null != typeElement )
          {
            final InjectableDescriptor candidate = deriveInjectableDescriptor( typeElement );
            if ( null != candidate && candidate.isAutoDiscoverable() )
            {
              assert coordinate.equals( candidate.getBinding().getPublishedServices().get( 0 ).getCoordinate() );
              _registry.registerInjectable( candidate );
              bindings.add( candidate.getBinding() );
            }
            if ( bindings.isEmpty() )
            {
              bindings.addAll( autoDiscoverProviderBindings( graph, workEntry, typeElement ) );
            }
          }
        }
      }

      final List<Binding> nullableProviders = bindings.stream()
        .filter( b -> b.getPublishedServices().stream().anyMatch( ServiceSpec::isOptional ) )
        .collect( Collectors.toList() );
      if ( !serviceRequest.canConsumeOptionalBindings() && !nullableProviders.isEmpty() )
      {
        final String message =
          MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                "contain an optional provider method or optional injector input and " +
                                "a non-optional service request for the coordinate " + coordinate + "\n" +
                                "Dependency Path:\n" + workEntry.describePathFromRoot() + "\n" +
                                "Binding" + ( nullableProviders.size() > 1 ? "s" : "" ) + ":\n" +
                                bindingsToString( nullableProviders ) );
        throw new ProcessorException( message, serviceRequest.getElement() );
      }
      if ( bindings.isEmpty() )
      {
        if ( serviceRequest.canBeAbsent() )
        {
          edge.setSatisfiedBy( Collections.emptyList() );
        }
        else
        {
          final String message =
            MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                  "contain a non-optional dependency " + coordinate +
                                  " that can not be satisfied.\n" +
                                  "Dependency Path:\n" + workEntry.describePathFromRoot() );
          throw new ProcessorException( message, serviceRequest.getElement() );
        }
      }
      else
      {
        final ServiceRequest.Kind kind = serviceRequest.getKind();
        if ( 1 == bindings.size() || kind.isCollection() )
        {
          final List<Node> nodes = new ArrayList<>();
          for ( final Binding binding : bindings )
          {
            final Node node = graph.findOrCreateNode( binding );
            nodes.add( node );
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
          assert bindings.size() > 1 && !kind.isCollection();
          final String message =
            MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                  "contain a non-collection dependency " + coordinate +
                                  " that can be satisfied by multiple nodes.\n" +
                                  "Dependency Path:\n" + workEntry.describePathFromRoot() + "\n" +
                                  "Candidate Nodes:\n" + bindingsToString( bindings ) );
          throw new ProcessorException( message, serviceRequest.getElement() );
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

  private void emitObjectGraphJsonDescriptor( @Nonnull final ComponentGraph graph )
    throws IOException
  {
    if ( _emitJsonDescriptors )
    {
      final TypeElement element = graph.getInjector().getElement();
      final String filename = toFilename( element ) + GRAPH_SUFFIX;
      debug( () -> "Emitting json descriptor for the injector " + graph.getInjector().getElement().getQualifiedName() );
      JsonUtil.writeJsonResource( processingEnv, element, filename, graph::write );
    }
  }

  @Nonnull
  private ResolveType isFragmentResolved( @Nonnull final RoundEnvironment env,
                                          @Nonnull final FragmentDescriptor fragment )
  {
    if ( fragment.isResolved() )
    {
      return ResolveType.RESOLVED;
    }
    else
    {
      final String fragmentName = fragment.getElement().getQualifiedName().toString();
      if ( _resolvingFragmentTypes.contains( fragmentName ) )
      {
        return ResolveType.RESOLVED;
      }
      _resolvingFragmentTypes.add( fragmentName );
      try
      {
        final ResolveType resolveType = isResolved( env, fragment, fragment.getElement(), fragment.getIncludes() );
        if ( resolveType == ResolveType.RESOLVED )
        {
          fragment.markAsResolved();
          // Check for redundant explicit @Injectable includes that are also transitively included via fragments
          maybeWarnOnRedundantDirectInjectableInclude( fragment.getElement(),
                                                       Constants.FRAGMENT_CLASSNAME,
                                                       fragment.getIncludes() );
          // Check for include cycles between fragments
          maybeWarnOnFragmentIncludeCycle( fragment.getElement(), fragment.getIncludes() );
        }
        return resolveType;
      }
      finally
      {
        _resolvingFragmentTypes.remove( fragmentName );
      }
    }
  }

  @Nonnull
  private ResolveType isInjectorResolved( @Nonnull final RoundEnvironment env,
                                          @Nonnull final InjectorDescriptor injector )
  {
    final ResolveType resolveType = isResolved( env, injector, injector.getElement(), injector.getIncludes() );
    if ( ResolveType.RESOLVED == resolveType )
    {
      // Check for redundant explicit @Injectable includes that are also transitively included via included fragments
      maybeWarnOnRedundantDirectInjectableInclude( injector.getElement(),
                                                   Constants.INJECTOR_CLASSNAME,
                                                   injector.getIncludes() );
    }
    return resolveType;
  }

  @Nonnull
  private <T> ResolveType isResolved( @Nonnull final RoundEnvironment env,
                                      @Nonnull final T descriptor,
                                      @Nonnull final TypeElement originator,
                                      @Nonnull final Collection<IncludeDescriptor> includes )
  {
    // By the time we get here we can guarantee that the java types for includes are correctly resolved
    // but they may not have passed through annotation processor and thus the descriptors may be absent
    // so we go through a few iterations and as long as one include is resolved in each iteration we should
    // keep going as we may be able to resolve all includes. Also if one of the includes is a provider
    // we need to check the associated provider type is resolved.
    for ( final IncludeDescriptor include : includes )
    {
      final ResolveType resolveType = isIncludeResolved( env, descriptor, originator, include );
      if ( ResolveType.RESOLVED != resolveType )
      {
        return resolveType;
      }
    }

    return ResolveType.RESOLVED;
  }

  @Nonnull
  private ResolveType isIncludeResolved( @Nonnull final RoundEnvironment env,
                                         @Nonnull final Object descriptor,
                                         @Nonnull final TypeElement originator,
                                         @Nonnull final IncludeDescriptor include )
  {
    AnnotationMirror annotation =
      AnnotationsUtil.findAnnotationByType( originator, Constants.INJECTOR_CLASSNAME );

    final String annotationClassname =
      null != annotation ? Constants.INJECTOR_CLASSNAME : Constants.FRAGMENT_CLASSNAME;
    if ( null == annotation )
    {
      annotation = AnnotationsUtil.getAnnotationByType( originator, Constants.FRAGMENT_CLASSNAME );
    }

    final String classname = include.getActualTypeName();
    final TypeElement element = processingEnv.getElementUtils().getTypeElement( classname );
    if ( null == element )
    {
      assert include.isProvider();
      if ( env.processingOver() )
      {
        if ( descriptor instanceof FragmentDescriptor )
        {
          ( (FragmentDescriptor) descriptor ).markAsContainsError();
        }
        else
        {
          ( (InjectorDescriptor) descriptor ).markAsContainsError();
        }

        final String message =
          MemberChecks.toSimpleName( annotationClassname ) + " target has an " +
          "parameter named 'includes' containing the value " + include.getIncludedType() +
          " and that type is annotated by the @StingProvider annotation. The provider annotation expects a " +
          "provider class named " + include.getActualTypeName() + " but no such class exists. The " +
          "type needs to be removed from the includes or the provider class needs to be present.";
        reportError( env, message, originator, annotation, null );
      }
      return ResolveType.UNRESOLVED;
    }
    final boolean isInjectable = AnnotationsUtil.hasAnnotationOfType( element, Constants.INJECTABLE_CLASSNAME );
    final boolean isFragment =
      !isInjectable && AnnotationsUtil.hasAnnotationOfType( element, Constants.FRAGMENT_CLASSNAME );
    if ( include.isProvider() && !isInjectable && !isFragment )
    {
      if ( descriptor instanceof FragmentDescriptor )
      {
        ( (FragmentDescriptor) descriptor ).markAsContainsError();
      }
      else
      {
        ( (InjectorDescriptor) descriptor ).markAsContainsError();
      }

      final String message =
        MemberChecks.toSimpleName( annotationClassname ) + " target has an " +
        "parameter named 'includes' containing the value " + include.getIncludedType() +
        " and that type is annotated by the @StingProvider annotation. The provider annotation expects a " +
        "provider class named " + include.getActualTypeName() + " but that class is not annotated with either " +
        MemberChecks.toSimpleName( Constants.INJECTOR_CLASSNAME ) + ", " +
        MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME ) + " or " +
        MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME );
      throw new ProcessorException( message, originator, annotation );
    }
    if ( descriptor instanceof final FragmentDescriptor fragmentDescriptor )
    {
      if ( fragmentDescriptor.isLocalOnly() && !isInSamePackage( originator, element ) )
      {
        fragmentDescriptor.markAsContainsError();
        final String message =
          MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME ) + " target has an includes parameter " +
          "containing the value " + include.getIncludedType() + " that is in the package " +
          GeneratorUtil.getQualifiedPackageName( element ) + " when the fragment is in the package " +
          GeneratorUtil.getQualifiedPackageName( originator ) + " and localOnly is true";
        throw new ProcessorException( message, originator, annotation );
      }
    }
    else if ( !include.isAuto() && ( (InjectorDescriptor) descriptor ).isFragmentOnly() && !isFragment )
    {
      final InjectorDescriptor injectorDescriptor = (InjectorDescriptor) descriptor;
      injectorDescriptor.markAsContainsError();
      final String message =
        MemberChecks.toSimpleName( Constants.INJECTOR_CLASSNAME ) + " target has an includes parameter containing " +
        "the value " + include.getIncludedType() + " that resolves to " + element.getQualifiedName() +
        " which is not annotated by " + MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME ) +
        " when fragmentOnly is true";
      throw new ProcessorException( message, originator, annotation );
    }
    if ( isFragment )
    {
      FragmentDescriptor fragment = _registry.findFragmentByClassName( classname );
      if ( null == fragment )
      {
        fragment = deriveFragmentDescriptor( element );
        if ( null == fragment )
        {
          debug( () -> "Unable to derive descriptor for fragment " + classname +
                       ". Marking " + originator.getQualifiedName() + " as unresolved" );
          return ResolveType.MAYBE_UNRESOLVED;
        }
        _registry.registerFragment( fragment );
      }
      if ( ResolveType.RESOLVED != isFragmentReady( env, fragment ) )
      {
        debug( () -> "Fragment include " + classname + " is present but not yet resolved. " +
                     "Marking " + originator.getQualifiedName() + " as unresolved" );
        return ResolveType.UNRESOLVED;
      }
    }
    else
    {
      InjectableDescriptor injectable = _registry.findInjectableByClassName( classname );
      if ( null == injectable )
      {
        injectable = deriveInjectableDescriptor( element );
        if ( null == injectable )
        {
          debug( () -> "Unable to derive descriptor for injectable " + classname +
                       ". Marking " + originator.getQualifiedName() + " as unresolved" );
          return ResolveType.MAYBE_UNRESOLVED;
        }
        _registry.registerInjectable( injectable );
      }
      if ( !SuperficialValidation.validateElement( processingEnv, injectable.getElement() ) )
      {
        debug( () -> "Injectable include " + classname + " is not yet resolved. " +
                     "Marking " + originator.getQualifiedName() + " as unresolved" );
        return ResolveType.UNRESOLVED;
      }

      if ( !include.isAuto() &&
           !injectable.getBinding().isEager() &&
           injectable.isAutoDiscoverable() &&
           ElementsUtil.isWarningNotSuppressed( originator, Constants.WARNING_AUTO_DISCOVERABLE_INCLUDED ) )
      {
        final String message =
          MemberChecks.shouldNot( annotationClassname,
                                  "include an auto-discoverable type " + classname + ". " +
                                  MemberChecks.suppressedBy( Constants.WARNING_AUTO_DISCOVERABLE_INCLUDED ) );
        warning( message, originator );
      }
    }
    return ResolveType.RESOLVED;
  }

  private void processInjectorFragment( @Nonnull final TypeElement element )
  {
    debug( () -> "Processing Injector Fragment: " + element );
    final ElementKind kind = element.getKind();
    if ( ElementKind.INTERFACE != kind )
    {
      throw new ProcessorException( MemberChecks.must( Constants.INJECTOR_FRAGMENT_CLASSNAME, "be an interface" ),
                                    element );
    }
    final List<ExecutableElement> methods =
      ElementsUtil.getMethods( element, processingEnv.getElementUtils(), processingEnv.getTypeUtils() );
    for ( final ExecutableElement method : methods )
    {
      if ( method.getModifiers().contains( Modifier.DEFAULT ) )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( Constants.INJECTOR_FRAGMENT_CLASSNAME ) +
                                      " target must not include default methods",
                                      method );
      }
      else if ( method.getModifiers().contains( Modifier.STATIC ) )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( Constants.INJECTOR_FRAGMENT_CLASSNAME ) +
                                      " target must not include static methods",
                                      method );
      }
    }
  }

  private void processInjector( @Nonnull final TypeElement element )
    throws Exception
  {
    debug( () -> "Processing Injector: " + element );
    final ElementKind kind = element.getKind();
    if ( ElementKind.INTERFACE != kind )
    {
      throw new ProcessorException( MemberChecks.must( Constants.INJECTOR_CLASSNAME, "be an interface" ),
                                    element );
    }
    if ( !element.getTypeParameters().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME, "have type parameters" ),
                                    element );
    }
    final List<? extends AnnotationMirror> scopedAnnotations = getScopedAnnotations( element );
    if ( !scopedAnnotations.isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                                          "be annotated with an annotation that is " +
                                                          "annotated with the " + Constants.JSR_330_SCOPE_CLASSNAME +
                                                          " annotation such as " + scopedAnnotations ),
                                    element );
    }

    final boolean gwt = isGwtEnabled( element );
    final boolean injectable =
      (boolean) AnnotationsUtil.getAnnotationValue( element, Constants.INJECTOR_CLASSNAME, "injectable" ).getValue();
    final boolean fragmentOnly = extractInjectorFragmentOnly( element );
    final List<IncludeDescriptor> includes = extractIncludes( element, Constants.INJECTOR_CLASSNAME, fragmentOnly );
    final List<InputDescriptor> inputs = extractInputs( element );

    final List<ServiceRequest> outputs = new ArrayList<>();
    final List<ExecutableElement> methods =
      ElementsUtil.getMethods( element, processingEnv.getElementUtils(), processingEnv.getTypeUtils() );
    for ( final ExecutableElement method : methods )
    {
      if ( method.getModifiers().contains( Modifier.ABSTRACT ) )
      {
        processInjectorOutputMethod( outputs, method );
      }
      else if ( method.getModifiers().contains( Modifier.DEFAULT ) )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( Constants.INJECTOR_CLASSNAME ) +
                                      " target must not include default methods",
                                      method );
      }
    }
    for ( final Element enclosedElement : element.getEnclosedElements() )
    {
      final ElementKind enclosedElementKind = enclosedElement.getKind();
      if ( ElementKind.INTERFACE == enclosedElementKind &&
           AnnotationsUtil.hasAnnotationOfType( enclosedElement, Constants.FRAGMENT_CLASSNAME ) )
      {
        final DeclaredType type = (DeclaredType) enclosedElement.asType();
        if ( includes.stream().noneMatch( d -> Objects.equals( d.getIncludedType(), type ) ) )
        {
          includes.add( new IncludeDescriptor( type, type.toString(), true ) );
        }
        else
        {
          throw new ProcessorException( MemberChecks.toSimpleName( Constants.INJECTOR_CLASSNAME ) +
                                        " target must not include a " +
                                        MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME ) + " annotated " +
                                        "type that is auto-included as it is enclosed within the injector type",
                                        element );
        }
      }
      else if ( ElementKind.CLASS == enclosedElementKind &&
                AnnotationsUtil.hasAnnotationOfType( enclosedElement, Constants.INJECTABLE_CLASSNAME ) )
      {
        final DeclaredType type = (DeclaredType) enclosedElement.asType();
        if ( includes.stream().noneMatch( d -> Objects.equals( d.getIncludedType(), type ) ) )
        {
          includes.add( new IncludeDescriptor( type, type.toString(), true ) );
        }
        else
        {
          throw new ProcessorException( MemberChecks.toSimpleName( Constants.INJECTOR_CLASSNAME ) +
                                        " target must not include an " +
                                        MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME ) + " annotated " +
                                        "type that is auto-included as it is enclosed within the injector type",
                                        element );
        }
      }
      else if ( enclosedElementKind.isClass() || enclosedElementKind.isInterface() )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( Constants.INJECTOR_CLASSNAME ) +
                                      " target must not contain a type that is not annotated " +
                                      "by either " + MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME ) +
                                      " or " + MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME ),
                                      element );
      }
    }
    final InjectorDescriptor injector =
      new InjectorDescriptor( element, gwt, injectable, fragmentOnly, includes, inputs, outputs );
    _registry.registerInjector( injector );
    emitInjectorJsonDescriptor( injector );
  }

  private boolean extractInjectorFragmentOnly( @Nonnull final TypeElement element )
  {
    return (boolean) AnnotationsUtil.getAnnotationValue( element, Constants.INJECTOR_CLASSNAME, "fragmentOnly" )
      .getValue();
  }

  private boolean isGwtEnabled( @Nonnull final TypeElement element )
  {
    final String value = AnnotationsUtil.getEnumAnnotationParameter( element, Constants.INJECTOR_CLASSNAME, "gwt" );
    return "ENABLE".equals( value ) ||
           ( "AUTODETECT".equals( value ) &&
             null != processingEnv.getElementUtils().getTypeElement( "javaemul.internal.annotations.DoNotInline" ) );
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

  private void processInjectorOutputMethod( @Nonnull final List<ServiceRequest> outputs,
                                            @Nonnull final ExecutableElement method )
  {
    assert method.getModifiers().contains( Modifier.ABSTRACT );
    if ( TypeKind.VOID == method.getReturnType().getKind() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                                          "contain a method that has a void return value" ),
                                    method );
    }
    else if ( !method.getParameters().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                                          "contain a method that has parameters" ),
                                    method );
    }
    else if ( !method.getTypeParameters().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                                          "contain a method that has any type parameters" ),
                                    method );
    }
    else if ( !method.getThrownTypes().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                                          "contain a method that throws any exceptions" ),
                                    method );
    }
    final List<? extends AnnotationMirror> scopedAnnotations = getScopedAnnotations( method );
    if ( !scopedAnnotations.isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                                          "contain a method that is annotated with an " +
                                                          "annotation that is annotated with the " +
                                                          Constants.JSR_330_SCOPE_CLASSNAME +
                                                          " annotation such as " + scopedAnnotations ),
                                    method );
    }
    outputs.add( processOutputMethod( method ) );
  }

  @Nonnull
  private ServiceRequest processOutputMethod( @Nonnull final ExecutableElement method )
  {
    final TypeMirror type = method.getReturnType();
    if ( TypesUtil.containsArrayType( type ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                                          "contain a method with a return type that contains an array type" ),
                                    method );
    }
    else if ( TypesUtil.containsWildcard( type ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                                          "contain a method with a return type that contains a wildcard type parameter" ),
                                    method );
    }
    else if ( TypesUtil.containsRawType( type ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                                          "contain a method with a return type that contains a raw type" ),
                                    method );
    }
    else
    {
      TypeMirror dependencyType = null;
      ServiceRequest.Kind kind = null;
      for ( final ServiceRequest.Kind candidate : ServiceRequest.Kind.values() )
      {
        dependencyType = candidate.extractType( type );
        if ( null != dependencyType )
        {
          kind = candidate;
          break;
        }
      }
      if ( null == kind )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                                            "contain a method with a return type that contains an unexpected parameterized type. Only parameterized types known to the framework are supported" ),
                                      method );
      }
      else
      {
        final boolean optional = AnnotationsUtil.hasNullableAnnotation( method );
        if ( optional && ServiceRequest.Kind.INSTANCE != kind )
        {
          throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                                              "contain a method annotated with " +
                                                              MemberChecks.toSimpleName( AnnotationsUtil.NULLABLE_CLASSNAME ) +
                                                              " that is not an instance dependency kind" ),
                                        method );
        }
        final String qualifier = getQualifier( method );
        if ( AnnotationsUtil.hasAnnotationOfType( method, Constants.JSR_330_NAMED_CLASSNAME ) )
        {
          throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                                              "contain a method annotated with the " +
                                                              Constants.JSR_330_NAMED_CLASSNAME +
                                                              " annotation. Use the " + Constants.NAMED_CLASSNAME +
                                                              " annotation instead" ),
                                        method );
        }

        final Coordinate coordinate = new Coordinate( qualifier, dependencyType );
        final ServiceSpec service = new ServiceSpec( coordinate, optional );
        return new ServiceRequest( kind, service, method, -1 );
      }
    }
  }

  private void verifyNamedElements( @Nonnull final RoundEnvironment env,
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
          !injectableType &&
          AnnotationsUtil.hasAnnotationOfType( executableElement.getEnclosingElement(), Constants.FRAGMENT_CLASSNAME );
        final ElementKind executableKind = executableElement.getKind();
        final boolean isProvider =
          !injectableType && !isFragmentType && hasStingProvider( executableElement.getEnclosingElement() );
        final boolean isActAsStingComponent =
          !injectableType &&
          !isFragmentType &&
          !isProvider &&
          hasActAsStingComponent( executableElement.getEnclosingElement() );
        if ( !injectableType && ElementKind.CONSTRUCTOR == executableKind && !isProvider && !isActAsStingComponent )
        {
          reportError( env,
                       MemberChecks.must( Constants.NAMED_CLASSNAME,
                                          "only be present on a constructor parameter if the constructor " +
                                          "is enclosed in a type annotated with " +
                                          MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME ) +
                                          " or the type is annotated with an annotation annotated by " +
                                          "@ActAsStringComponent or @StingProvider" ),
                       element );
        }
        else if ( !isFragmentType && ElementKind.METHOD == executableKind )
        {
          reportError( env,
                       MemberChecks.must( Constants.NAMED_CLASSNAME,
                                          "only be present on a method parameter if the method is enclosed in a type annotated with " +
                                          MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME ) ),
                       element );
        }
        else
        {
          assert ( injectableType && ElementKind.CONSTRUCTOR == executableKind ) ||
                 ( isProvider && ElementKind.CONSTRUCTOR == executableKind ) ||
                 ( isActAsStingComponent && ElementKind.CONSTRUCTOR == executableKind ) ||
                 ( isFragmentType && ElementKind.METHOD == executableKind );
        }
      }
      else if ( ElementKind.CLASS == element.getKind() )
      {
        if ( !AnnotationsUtil.hasAnnotationOfType( element, Constants.INJECTABLE_CLASSNAME ) &&
             !hasStingProvider( element ) &&
             !hasActAsStingComponent( element ) )
        {
          reportError( env,
                       MemberChecks.must( Constants.NAMED_CLASSNAME,
                                          "only be present on a type if the type is annotated with " +
                                          MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME ) +
                                          " or the type is annotated with an annotation annotated by " +
                                          "@ActAsStringComponent or @StingProvider" ),
                       element );
        }
      }
      else if ( ElementKind.METHOD == element.getKind() )
      {
        if ( !AnnotationsUtil.hasAnnotationOfType( element.getEnclosingElement(), Constants.FRAGMENT_CLASSNAME ) &&
             !AnnotationsUtil.hasAnnotationOfType( element.getEnclosingElement(), Constants.INJECTOR_CLASSNAME ) &&
             !AnnotationsUtil.hasAnnotationOfType( element.getEnclosingElement(),
                                                   Constants.INJECTOR_FRAGMENT_CLASSNAME ) )
        {
          reportError( env,
                       MemberChecks.mustNot( Constants.NAMED_CLASSNAME,
                                             "be a method unless the method is enclosed in a type annotated with " +
                                             MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME ) + ", " +
                                             MemberChecks.toSimpleName( Constants.INJECTOR_CLASSNAME ) + " or " +
                                             MemberChecks.toSimpleName( Constants.INJECTOR_FRAGMENT_CLASSNAME ) ),
                       element );
        }
      }
      else
      {
        reportError( env,
                     MemberChecks.toSimpleName( Constants.NAMED_CLASSNAME ) + " target is not valid",
                     element );
      }
    }
  }

  private boolean hasStingProvider( @Nonnull final Element element )
  {
    return hasAnnotationWithAnnotationMatching( element,
                                                ca -> ca.getAnnotationType()
                                                  .asElement()
                                                  .getSimpleName()
                                                  .contentEquals( "StingProvider" ) );
  }

  private boolean hasActAsStingComponent( @Nonnull final Element element )
  {
    return hasAnnotationWithAnnotationMatching( element,
                                                ca -> ca.getAnnotationType()
                                                  .asElement()
                                                  .getSimpleName()
                                                  .contentEquals( "ActAsStingComponent" ) );
  }

  private boolean hasAnnotationWithAnnotationMatching( @Nonnull final AnnotatedConstruct element,
                                                       @Nonnull final Predicate<? super AnnotationMirror> predicate )
  {
    return element
      .getAnnotationMirrors()
      .stream()
      .anyMatch( a -> a.getAnnotationType()
        .asElement()
        .getAnnotationMirrors()
        .stream()
        .anyMatch( predicate ) );
  }

  private void verifyTypedElements( @Nonnull final RoundEnvironment env,
                                    @Nonnull final Set<? extends Element> elements )
  {
    for ( final Element element : elements )
    {
      if ( ElementKind.CLASS == element.getKind() )
      {
        if ( !AnnotationsUtil.hasAnnotationOfType( element, Constants.INJECTABLE_CLASSNAME ) &&
             !hasStingProvider( element ) )
        {
          reportError( env,
                       MemberChecks.must( Constants.TYPED_CLASSNAME,
                                          "only be present on a type if the type is annotated with " +
                                          MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME ) +
                                          " or the type is annotated with an annotation annotated by @StingProvider" ),
                       element );
        }
      }
      else if ( ElementKind.METHOD == element.getKind() )
      {
        if ( !AnnotationsUtil.hasAnnotationOfType( element.getEnclosingElement(), Constants.FRAGMENT_CLASSNAME ) &&
             !AnnotationsUtil.hasAnnotationOfType( element.getEnclosingElement(), Constants.INJECTOR_CLASSNAME ) )
        {
          reportError( env,
                       MemberChecks.mustNot( Constants.TYPED_CLASSNAME,
                                             "be a method unless the method is enclosed in a type annotated with " +
                                             MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME ) + " or " +
                                             MemberChecks.toSimpleName( Constants.INJECTOR_CLASSNAME ) ),
                       element );
        }
      }
      else
      {
        reportError( env,
                     MemberChecks.toSimpleName( Constants.TYPED_CLASSNAME ) + " target is not valid",
                     element );
      }
    }
  }

  private void verifyEagerElements( @Nonnull final RoundEnvironment env,
                                    @Nonnull final Set<? extends Element> elements )
  {
    for ( final Element element : elements )
    {
      if ( ElementKind.CLASS == element.getKind() )
      {
        if ( !AnnotationsUtil.hasAnnotationOfType( element, Constants.INJECTABLE_CLASSNAME ) &&
             !hasStingProvider( element ) )
        {
          reportError( env,
                       MemberChecks.must( Constants.EAGER_CLASSNAME,
                                          "only be present on a type if the type is annotated with " +
                                          MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME ) +
                                          " or the type is annotated with an annotation annotated by @StingProvider" ),
                       element );
        }
      }
      else if ( ElementKind.METHOD == element.getKind() )
      {
        if ( !AnnotationsUtil.hasAnnotationOfType( element.getEnclosingElement(), Constants.FRAGMENT_CLASSNAME ) )
        {
          reportError( env,
                       MemberChecks.must( Constants.EAGER_CLASSNAME,
                                          "only be present on a method if the method is enclosed in a type annotated with " +
                                          MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME ) ),
                       element );
        }
      }
      else
      {
        reportError( env,
                     MemberChecks.toSimpleName( Constants.EAGER_CLASSNAME ) + " target is not valid",
                     element );
      }
    }
  }

  private void processFragment( @Nonnull final TypeElement element )
  {
    debug( () -> "Processing Fragment: " + element );
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
    else if ( !element.getInterfaces().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME, "extend any interfaces" ),
                                    element );
    }
    final boolean localOnly = extractFragmentLocalOnly( element );
    final List<IncludeDescriptor> includes = extractIncludes( element, Constants.FRAGMENT_CLASSNAME, false );
    final Map<ExecutableElement, Binding> bindings = new LinkedHashMap<>();
    for ( final Element enclosedElement : element.getEnclosedElements() )
    {
      final ElementKind enclosedElementKind = enclosedElement.getKind();
      if ( ElementKind.METHOD == enclosedElementKind )
      {
        processProvidesMethod( element, bindings, (ExecutableElement) enclosedElement );
      }
      if ( enclosedElementKind.isClass() || enclosedElementKind.isInterface() )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME ) +
                                      " target must not contain any types",
                                      element );
      }
    }
    if ( bindings.isEmpty() && includes.isEmpty() )
    {
      throw new ProcessorException( MemberChecks.must( Constants.FRAGMENT_CLASSNAME,
                                                       "contain one or more methods or one or more includes" ),
                                    element );
    }
    final List<? extends AnnotationMirror> scopedAnnotations = getScopedAnnotations( element );
    if ( !scopedAnnotations.isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                          "be annotated with an annotation that is " +
                                                          "annotated with the " + Constants.JSR_330_SCOPE_CLASSNAME +
                                                          " annotation such as " + scopedAnnotations ),
                                    element );
    }
    _registry.registerFragment( new FragmentDescriptor( element, includes, localOnly, bindings.values() ) );
  }

  private void processFactory( @Nonnull final TypeElement element )
  {
    debug( () -> "Processing Factory: " + element );
    if ( ElementKind.INTERFACE != element.getKind() )
    {
      throw new ProcessorException( MemberChecks.must( Constants.FACTORY_CLASSNAME, "be an interface" ),
                                    element );
    }
    else if ( !element.getTypeParameters().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FACTORY_CLASSNAME, "have type parameters" ),
                                    element );
    }
    else if ( !element.getInterfaces().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FACTORY_CLASSNAME, "extend any interfaces" ),
                                    element );
    }

    final List<FactoryMethodDescriptor> methods = new ArrayList<>();
    final List<FactoryDependencyDescriptor> dependencies = new ArrayList<>();
    final Set<String> usedFieldNames = new HashSet<>();
    for ( final Element enclosedElement : element.getEnclosedElements() )
    {
      if ( ElementKind.METHOD == enclosedElement.getKind() )
      {
        final ExecutableElement method = (ExecutableElement) enclosedElement;
        if ( !method.getModifiers().contains( Modifier.DEFAULT ) &&
             !method.getModifiers().contains( Modifier.STATIC ) &&
             !method.getModifiers().contains( Modifier.PRIVATE ) )
        {
          methods.add( processFactoryMethod( element, method, dependencies, usedFieldNames ) );
        }
      }
    }
    if ( methods.isEmpty() )
    {
      throw new ProcessorException( MemberChecks.must( Constants.FACTORY_CLASSNAME,
                                                       "contain at least one abstract method that returns a value" ),
                                    element );
    }

    _registry.registerFactory( new FactoryDescriptor( element, methods, dependencies ) );
  }

  @SuppressWarnings( "unchecked" )
  @Nonnull
  private List<InputDescriptor> extractInputs( @Nonnull final TypeElement element )
  {
    final List<InputDescriptor> results = new ArrayList<>();
    final AnnotationMirror annotation = AnnotationsUtil.getAnnotationByType( element, Constants.INJECTOR_CLASSNAME );
    final AnnotationValue inputsAnnotationValue = AnnotationsUtil.findAnnotationValue( annotation, "inputs" );
    assert null != inputsAnnotationValue;
    final List<AnnotationMirror> inputs = (List<AnnotationMirror>) inputsAnnotationValue.getValue();

    final int size = inputs.size();
    for ( int i = 0; i < size; i++ )
    {
      final AnnotationMirror input = inputs.get( i );
      final String qualifier = AnnotationsUtil.getAnnotationValueValue( input, "qualifier" );
      final AnnotationValue typeAnnotationValue = AnnotationsUtil.getAnnotationValue( input, "type" );
      final TypeMirror type = (TypeMirror) typeAnnotationValue.getValue();
      if ( TypeKind.ARRAY == type.getKind() )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( Constants.INPUT_CLASSNAME ) +
                                      " must not specify an array type for the type parameter",
                                      element,
                                      input,
                                      typeAnnotationValue );
      }
      else if ( TypeKind.VOID == type.getKind() )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( Constants.INPUT_CLASSNAME ) +
                                      " must specify a non-void type for the type parameter",
                                      element,
                                      input,
                                      typeAnnotationValue );
      }
      else if ( TypeKind.DECLARED == type.getKind() &&
                !( (TypeElement) ( (DeclaredType) type ).asElement() ).getTypeParameters().isEmpty() )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( Constants.INPUT_CLASSNAME ) +
                                      " must not specify a parameterized type for the type parameter",
                                      element,
                                      input,
                                      typeAnnotationValue );
      }
      final Coordinate coordinate = new Coordinate( qualifier, type );
      final boolean optional = AnnotationsUtil.getAnnotationValueValue( input, "optional" );
      final ServiceSpec service = new ServiceSpec( coordinate, optional );
      final Binding binding =
        new Binding( Binding.Kind.INPUT,
                     element.getQualifiedName() + "#" + i,
                     Collections.singletonList( service ),
                     true,
                     element,
                     new ServiceRequest[ 0 ] );
      results.add( new InputDescriptor( service, binding, "input" + ( i + 1 ) ) );
    }
    return results;
  }

  @Nonnull
  private List<IncludeDescriptor> extractIncludes( @Nonnull final TypeElement element,
                                                   @Nonnull final String annotationClassname,
                                                   final boolean fragmentOnly )
  {
    final List<IncludeDescriptor> results = new ArrayList<>();
    final List<TypeMirror> includes =
      AnnotationsUtil.getTypeMirrorsAnnotationParameter( element, annotationClassname, "includes" );
    final Set<String> included = new HashSet<>();
    for ( final TypeMirror include : includes )
    {
      if ( processingEnv.getTypeUtils().isSameType( include, element.asType() ) )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( annotationClassname ) +
                                      " target must not include self",
                                      element );
      }
      if ( include.getKind().isPrimitive() )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( annotationClassname ) +
                                      " target must not include a primitive in the includes parameter",
                                      element );
      }
      final Element includeElement = processingEnv.getTypeUtils().asElement( include );
      if ( AnnotationsUtil.hasAnnotationOfType( includeElement, Constants.FRAGMENT_CLASSNAME ) ||
           AnnotationsUtil.hasAnnotationOfType( includeElement, Constants.INJECTABLE_CLASSNAME ) )
      {
        if ( fragmentOnly &&
             AnnotationsUtil.hasAnnotationOfType( includeElement, Constants.INJECTABLE_CLASSNAME ) )
        {
          throw new ProcessorException( MemberChecks.toSimpleName( annotationClassname ) +
                                        " target has an includes parameter containing the value " + include +
                                        " that is not annotated by " +
                                        MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME ) +
                                        " when fragmentOnly is true",
                                        element );
        }
        results.add( new IncludeDescriptor( (DeclaredType) include, include.toString(), false ) );
      }
      else
      {
        final ElementKind kind = includeElement.getKind();
        if ( ElementKind.CLASS == kind || ElementKind.INTERFACE == kind )
        {
          final ProviderEntry provider =
            resolveSingleStingProvider( element,
                                        MemberChecks.toSimpleName( annotationClassname ) +
                                        " target has an 'includes' parameter containing the value " +
                                        includeElement.asType(),
                                        (TypeElement) includeElement );
          if ( null != provider )
          {
            final String targetQualifiedName =
              deriveProviderQualifiedName( (TypeElement) includeElement, provider.getProvider() );
            results.add( new IncludeDescriptor( (DeclaredType) include, targetQualifiedName, false ) );
          }
          else
          {
            throw new ProcessorException( MemberChecks.toSimpleName( annotationClassname ) +
                                          " target has an includes parameter containing the value " + include +
                                          " that is not a type annotated by either " +
                                          MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME ) +
                                          " or " +
                                          MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME ) +
                                          " and the type does not declare a provider",
                                          element );
          }
        }
      }
      final String includedType = include.toString();
      if ( included.contains( includedType ) )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( annotationClassname ) +
                                      " target has an includes parameter containing duplicate " +
                                      "includes with the type " + includedType,
                                      element );
      }
      else
      {
        included.add( includedType );
      }
    }
    return results;
  }

  @Nullable
  private ProviderEntry resolveSingleStingProvider( @Nonnull final TypeElement element,
                                                    @Nonnull final String targetDescription,
                                                    @Nonnull final TypeElement annotatedType )
  {
    final List<ProviderEntry> providers =
      annotatedType.getAnnotationMirrors()
        .stream()
        .map( a -> {
          final AnnotationMirror provider = getStingProvider( element, targetDescription, annotatedType, a );
          return null != provider ? new ProviderEntry( a, provider ) : null;
        } )
        .filter( Objects::nonNull )
        .toList();
    if ( providers.size() > 1 )
    {
      final String message =
        targetDescription + " that is annotated by multiple @StingProvider annotations. Matching annotations:\n" +
        providers
          .stream()
          .map( a -> ( (TypeElement) a.getAnnotation().getAnnotationType().asElement() ).getQualifiedName() )
          .map( a -> "  " + a )
          .collect( Collectors.joining( "\n" ) );
      throw new ProcessorException( message, element );
    }
    return providers.isEmpty() ? null : providers.get( 0 );
  }

  @Nullable
  private AnnotationMirror getStingProvider( @Nonnull final TypeElement element,
                                             @Nonnull final String targetDescription,
                                             @Nonnull final TypeElement annotatedType,
                                             @Nonnull final AnnotationMirror annotation )
  {
    return annotation.getAnnotationType()
      .asElement()
      .getAnnotationMirrors()
      .stream()
      .filter( ca -> isStingProvider( element, targetDescription, annotatedType, ca ) )
      .findAny()
      .orElse( null );
  }

  @Nonnull
  private String deriveProviderQualifiedName( @Nonnull final TypeElement annotatedType,
                                              @Nonnull final AnnotationMirror providerAnnotation )
  {
    final String namePattern = AnnotationsUtil.getAnnotationValueValue( providerAnnotation, "value" );
    final String targetCompoundType =
      namePattern
        .replace( "[SimpleName]", annotatedType.getSimpleName().toString() )
        .replace( "[CompoundName]", getComponentName( annotatedType ) )
        .replace( "[EnclosingName]", getEnclosingName( annotatedType ) )
        .replace( "[FlatEnclosingName]", getEnclosingName( annotatedType ).replace( '.', '_' ) );
    return ElementsUtil.getPackageElement( annotatedType ).getQualifiedName().toString() + "." + targetCompoundType;
  }

  @Nonnull
  private String getComponentName( @Nonnull final TypeElement element )
  {
    return getEnclosingName( element ) + element.getSimpleName();
  }

  @Nonnull
  private String getEnclosingName( @Nonnull final TypeElement element )
  {
    Element enclosingElement = element.getEnclosingElement();
    final List<String> nameParts = new ArrayList<>();
    while ( ElementKind.PACKAGE != enclosingElement.getKind() )
    {
      nameParts.add( enclosingElement.getSimpleName().toString() );
      enclosingElement = enclosingElement.getEnclosingElement();
    }
    if ( nameParts.isEmpty() )
    {
      return "";
    }
    else
    {
      Collections.reverse( nameParts );
      return String.join( ".", nameParts ) + ".";
    }
  }

  private boolean isStingProvider( @Nonnull final TypeElement element,
                                   @Nonnull final String targetDescription,
                                   @Nonnull final Element annotatedType,
                                   @Nonnull final AnnotationMirror annotation )
  {
    if ( !annotation.getAnnotationType().asElement().getSimpleName().contentEquals( "StingProvider" ) )
    {
      return false;
    }
    else
    {
      final boolean nameMatched = annotation.getElementValues()
        .entrySet()
        .stream()
        .anyMatch( e -> e.getKey().getSimpleName().contentEquals( "value" ) &&
                        e.getValue().getValue() instanceof String );
      if ( nameMatched )
      {
        return true;
      }
      else
      {
        final String message = targetDescription +
                               " that is annotated by " + annotation +
                               " that is annotated by an invalid @StingProvider " +
          "annotation missing a 'value' parameter of type string.";
        throw new ProcessorException( message, element );
      }
    }
  }

  @Nonnull
  private List<Binding> autoDiscoverProviderBindings( @Nonnull final ComponentGraph graph,
                                                      @Nonnull final WorkEntry workEntry,
                                                      @Nonnull final TypeElement frameworkType )
  {
    final ProviderEntry provider =
      resolveSingleStingProvider( graph.getInjector().getElement(),
                                  MemberChecks.toSimpleName( Constants.INJECTOR_CLASSNAME ) +
                                  " target is attempting to auto-discover the type " + frameworkType.asType(),
                                  frameworkType );
    if ( null == provider )
    {
      return Collections.emptyList();
    }

    final String providerTypeName = deriveProviderQualifiedName( frameworkType, provider.getProvider() );
    final TypeElement providerType = processingEnv.getElementUtils().getTypeElement( providerTypeName );
    final Coordinate coordinate = new Coordinate( "", frameworkType.asType() );
    if ( null == providerType )
    {
      throw new ProcessorException( buildAutoDiscoverProviderError( coordinate,
                                                                    workEntry,
                                                                    "the framework type " +
                                                                    frameworkType.getQualifiedName() +
                                                                    " expects a provider class named " +
                                                                    providerTypeName + " but no such class exists" ),
                                    workEntry.getEntry().getEdge().getServiceRequest().getElement() );
    }

    if ( AnnotationsUtil.hasAnnotationOfType( providerType, Constants.FRAGMENT_CLASSNAME ) )
    {
      FragmentDescriptor fragment = _registry.findFragmentByClassName( providerTypeName );
      if ( null == fragment )
      {
        fragment = deriveFragmentDescriptor( providerType );
        if ( null != fragment )
        {
          _registry.registerFragment( fragment );
        }
      }
      if ( null == fragment )
      {
        return Collections.emptyList();
      }
      verifyAutoDiscoverProviderPublishesType( coordinate, providerType, fragment.getBindings(), workEntry );
      registerAutoDiscoveredFragment( graph, fragment );
      return graph.findAllBindingsByCoordinate( coordinate );
    }
    else if ( AnnotationsUtil.hasAnnotationOfType( providerType, Constants.INJECTABLE_CLASSNAME ) )
    {
      InjectableDescriptor injectable = _registry.findInjectableByClassName( providerTypeName );
      if ( null == injectable )
      {
        injectable = deriveInjectableDescriptor( providerType );
        if ( null != injectable )
        {
          _registry.registerInjectable( injectable );
        }
      }
      if ( null == injectable )
      {
        return Collections.emptyList();
      }
      verifyAutoDiscoverProviderPublishesType( coordinate,
                                               providerType,
                                               Collections.singletonList( injectable.getBinding() ),
                                               workEntry );
      graph.registerInjectable( injectable );
      return Collections.singletonList( injectable.getBinding() );
    }
    else
    {
      throw new ProcessorException( buildAutoDiscoverProviderError( coordinate,
                                                                    workEntry,
                                                                    "the framework type " +
                                                                    frameworkType.getQualifiedName() +
                                                                    " expects a provider class named " +
                                                                    providerTypeName +
                                                                    " but that class is not annotated with either " +
                                                                    MemberChecks.toSimpleName(
                                                                      Constants.FRAGMENT_CLASSNAME ) +
                                                                    " or " +
                                                                    MemberChecks.toSimpleName(
                                                                      Constants.INJECTABLE_CLASSNAME ) ),
                                    workEntry.getEntry().getEdge().getServiceRequest().getElement() );
    }
  }

  private void registerAutoDiscoveredFragment( @Nonnull final ComponentGraph graph,
                                               @Nonnull final FragmentDescriptor fragment )
  {
    registerAutoDiscoveredIncludes( graph, fragment.getIncludes() );
    graph.registerFragment( fragment );
  }

  private void registerAutoDiscoveredIncludes( @Nonnull final ComponentGraph graph,
                                               @Nonnull final Collection<IncludeDescriptor> includes )
  {
    for ( final IncludeDescriptor include : includes )
    {
      final String classname = include.getActualTypeName();
      final TypeElement element = processingEnv.getElementUtils().getTypeElement( classname );
      assert null != element;
      if ( AnnotationsUtil.hasAnnotationOfType( element, Constants.FRAGMENT_CLASSNAME ) )
      {
        FragmentDescriptor fragment = _registry.findFragmentByClassName( classname );
        if ( null == fragment )
        {
          fragment = deriveFragmentDescriptor( element );
          if ( null != fragment )
          {
            _registry.registerFragment( fragment );
          }
        }
        assert null != fragment;
        registerAutoDiscoveredFragment( graph, fragment );
      }
      else
      {
        assert AnnotationsUtil.hasAnnotationOfType( element, Constants.INJECTABLE_CLASSNAME );
        InjectableDescriptor injectable = _registry.findInjectableByClassName( classname );
        if ( null == injectable )
        {
          injectable = deriveInjectableDescriptor( element );
          if ( null != injectable )
          {
            _registry.registerInjectable( injectable );
          }
        }
        assert null != injectable;
        graph.registerInjectable( injectable );
      }
    }
  }

  private void verifyAutoDiscoverProviderPublishesType( @Nonnull final Coordinate coordinate,
                                                        @Nonnull final TypeElement providerType,
                                                        @Nonnull final Collection<Binding> bindings,
                                                        @Nonnull final WorkEntry workEntry )
  {
    final boolean matches =
      bindings.stream().flatMap( b -> b.getPublishedServices().stream() ).anyMatch( s -> coordinate.equals(
        s.getCoordinate() ) );
    if ( !matches )
    {
      throw new ProcessorException( buildAutoDiscoverProviderError( coordinate,
                                                                    workEntry,
                                                                    "the provider class " +
                                                                    providerType.getQualifiedName() +
                                                                    " does not publish the service " +
                                                                    coordinate +
                                                                    " for the framework type " +
                                                                    coordinate.getType() +
                                                                    ". The provider must publish the framework type with the default qualifier" ),
                                    workEntry.getEntry().getEdge().getServiceRequest().getElement() );
    }
  }

  @Nonnull
  private String buildAutoDiscoverProviderError( @Nonnull final Coordinate coordinate,
                                                 @Nonnull final WorkEntry workEntry,
                                                 @Nonnull final String detail )
  {
    return MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                 "contain a non-optional dependency " + coordinate +
                                 " that can not be auto-discovered via @StingProvider because " +
                                 detail + ".\n" +
                                 "Dependency Path:\n" + workEntry.describePathFromRoot() );
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
    if ( TypeKind.VOID == method.getReturnType().getKind() )
    {
      throw new ProcessorException( MemberChecks.must( Constants.FRAGMENT_CLASSNAME,
                                                       "only contain methods that return a value" ),
                                    method );
    }
    if ( !method.getTypeParameters().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                          "contain methods with a type parameter" ),
                                    method );
    }
    if ( !method.getModifiers().contains( Modifier.DEFAULT ) )
    {
      throw new ProcessorException( MemberChecks.must( Constants.FRAGMENT_CLASSNAME,
                                                       "only contain methods with a default modifier" ),
                                    method );
    }
    final boolean nullablePresent = AnnotationsUtil.hasNullableAnnotation( method );
    if ( nullablePresent && method.getReturnType().getKind().isPrimitive() )
    {
      throw new ProcessorException( MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME ) +
                                    " contains a method that is incorrectly annotated with " +
                                    MemberChecks.toSimpleName( AnnotationsUtil.NULLABLE_CLASSNAME ) +
                                    " as the return type is a primitive value",
                                    method );
    }
    final List<? extends AnnotationMirror> scopedAnnotations = getScopedAnnotations( method );
    if ( !scopedAnnotations.isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                          "contain a method that is annotated with an " +
                                                          "annotation that is annotated with the " +
                                                          Constants.JSR_330_SCOPE_CLASSNAME +
                                                          " annotation such as " + scopedAnnotations ),
                                    method );
    }

    final boolean eager = AnnotationsUtil.hasAnnotationOfType( method, Constants.EAGER_CLASSNAME );

    final List<ServiceRequest> dependencies = new ArrayList<>();
    int index = 0;
    final List<? extends TypeMirror> parameterTypes = ( (ExecutableType) method.asType() ).getParameterTypes();
    for ( final VariableElement parameter : method.getParameters() )
    {
      dependencies.add( processFragmentServiceParameter( parameter, parameterTypes.get( index ), index ) );
      index++;
    }

    final AnnotationMirror annotation = AnnotationsUtil.findAnnotationByType( method, Constants.TYPED_CLASSNAME );
    final AnnotationValue value =
      null != annotation ? AnnotationsUtil.findAnnotationValue( annotation, "value" ) : null;

    final String qualifier = getQualifier( method );
    if ( AnnotationsUtil.hasAnnotationOfType( method, Constants.JSR_330_NAMED_CLASSNAME ) )
    {
      final String message =
        MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                              "contain a method annotated with the " + Constants.JSR_330_NAMED_CLASSNAME +
                              " annotation. Use the " + Constants.NAMED_CLASSNAME + " annotation instead" );
      throw new ProcessorException( message, method );
    }
    if ( AnnotationsUtil.hasAnnotationOfType( method, Constants.CDI_TYPED_CLASSNAME ) )
    {
      final String message =
        MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                              "contain a method annotated with the " + Constants.CDI_TYPED_CLASSNAME +
                              " annotation. Use the " + Constants.TYPED_CLASSNAME + " annotation instead" );
      throw new ProcessorException( message, method );
    }

    @SuppressWarnings( "unchecked" )
    final List<TypeMirror> types =
      null == value ?
      Collections.singletonList( method.getReturnType() ) :
      ( (List<AnnotationValue>) value.getValue() )
        .stream()
        .map( v -> (TypeMirror) v.getValue() )
        .toList();

    final ServiceSpec[] specs = new ServiceSpec[ types.size() ];
    for ( int i = 0; i < specs.length; i++ )
    {
      final TypeMirror type = types.get( i );
      if ( !processingEnv.getTypeUtils().isAssignable( method.getReturnType(), type ) )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( Constants.TYPED_CLASSNAME ) +
                                      " specified a type that is not assignable to the return type of the method",
                                      element,
                                      annotation,
                                      value );
      }
      else if ( TypeKind.DECLARED == type.getKind() && isParameterized( (DeclaredType) type ) )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( Constants.TYPED_CLASSNAME ) +
                                      " specified a type that is a a parameterized type",
                                      element,
                                      annotation,
                                      value );
      }
      specs[ i ] = new ServiceSpec( new Coordinate( qualifier, type ), nullablePresent );
    }

    if ( 0 == specs.length && !eager )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                          "contain methods that specify zero types with the " +
                                                          MemberChecks.toSimpleName( Constants.TYPED_CLASSNAME ) +
                                                          " annotation and are not annotated with the " +
                                                          MemberChecks.toSimpleName( Constants.EAGER_CLASSNAME ) +
                                                          " annotation otherwise the component can not be created by " +
                                                          "the injector" ),
                                    element );
    }
    if ( 0 == specs.length && !qualifier.isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                          "contain methods that specify zero types with the " +
                                                          MemberChecks.toSimpleName( Constants.TYPED_CLASSNAME ) +
                                                          " annotation and specify a qualifier with the " +
                                                          MemberChecks.toSimpleName( Constants.NAMED_CLASSNAME ) +
                                                          " annotation as the qualifier is meaningless" ),
                                    element );
    }

    final Binding binding =
      new Binding( Binding.Kind.PROVIDES,
                   element.getQualifiedName() + "#" + method.getSimpleName(),
                   Arrays.asList( specs ),
                   eager,
                   method,
                   dependencies.toArray( new ServiceRequest[ 0 ] ) );
    bindings.put( method, binding );
  }

  @Nonnull
  private ServiceRequest processFragmentServiceParameter( @Nonnull final VariableElement parameter,
                                                          @Nonnull final TypeMirror type,
                                                          final int parameterIndex )
  {
    if ( TypesUtil.containsArrayType( type ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                          "contain a method with a parameter that contains an array type" ),
                                    parameter );
    }
    else if ( TypesUtil.containsWildcard( type ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                          "contain a method with a parameter that contains a wildcard type parameter" ),
                                    parameter );
    }
    else if ( TypesUtil.containsRawType( type ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                          "contain a method with a parameter that contains a raw type" ),
                                    parameter );
    }
    else
    {
      TypeMirror dependencyType = null;
      ServiceRequest.Kind kind = null;
      for ( final ServiceRequest.Kind candidate : ServiceRequest.Kind.values() )
      {
        dependencyType = candidate.extractType( type );
        if ( null != dependencyType )
        {
          kind = candidate;
          break;
        }
      }
      if ( null == kind )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                            "contain a method with a parameter that contains an unexpected parameterized type. Only parameterized types known to the framework are supported" ),
                                      parameter );
      }
      else
      {
        final boolean optional = AnnotationsUtil.hasNullableAnnotation( parameter );
        if ( optional && ServiceRequest.Kind.INSTANCE != kind )
        {
          throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                              "contain a method with a parameter annotated with the " +
                                                              MemberChecks.toSimpleName( AnnotationsUtil.NULLABLE_CLASSNAME ) +
                                                              " annotation that is not an instance dependency kind" ),
                                        parameter );
        }
        final String qualifier = getQualifier( parameter );
        if ( AnnotationsUtil.hasAnnotationOfType( parameter, Constants.JSR_330_NAMED_CLASSNAME ) )
        {
          final String message =
            MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                  "contain a method with a parameter annotated with the " +
                                  Constants.JSR_330_NAMED_CLASSNAME + " annotation. Use the " +
                                  Constants.NAMED_CLASSNAME + " annotation instead" );
          throw new ProcessorException( message, parameter );
        }
        final Coordinate coordinate = new Coordinate( qualifier, dependencyType );
        final ServiceSpec service = new ServiceSpec( coordinate, optional );
        return new ServiceRequest( kind, service, parameter, parameterIndex );
      }
    }
  }

  private void processInjectable( @Nonnull final TypeElement element )
  {
    debug( () -> "Processing Injectable: " + element );
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
    final List<ExecutableElement> constructors = ElementsUtil.getConstructors( element );
    final ExecutableElement constructor = constructors.get( 0 );
    if ( constructors.size() > 1 )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                          "have multiple constructors" ),
                                    element );
    }
    injectableConstructorShouldNotBeProtected( constructor );
    injectableConstructorShouldNotBePublic( constructor );
    injectableShouldNotHaveScopedAnnotation( element );

    final boolean eager = AnnotationsUtil.hasAnnotationOfType( element, Constants.EAGER_CLASSNAME );

    final List<ServiceRequest> dependencies = new ArrayList<>();
    int index = 0;
    final List<? extends TypeMirror> parameterTypes = ( (ExecutableType) constructor.asType() ).getParameterTypes();
    for ( final VariableElement parameter : constructor.getParameters() )
    {
      dependencies.add( handleConstructorParameter( parameter, parameterTypes.get( index ), index ) );
      index++;
    }

    final AnnotationMirror annotation =
      AnnotationsUtil.findAnnotationByType( element, Constants.TYPED_CLASSNAME );
    final AnnotationValue value =
      null != annotation ? AnnotationsUtil.findAnnotationValue( annotation, "value" ) : null;

    final String qualifier = getQualifier( element );
    if ( AnnotationsUtil.hasAnnotationOfType( element, Constants.JSR_330_NAMED_CLASSNAME ) &&
         ElementsUtil.isWarningNotSuppressed( element, Constants.WARNING_JSR_330_NAMED ) )
    {
      final String message =
        MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                              "be annotated with the " + Constants.JSR_330_NAMED_CLASSNAME + " annotation. " +
                              "Use the " + Constants.NAMED_CLASSNAME + " annotation instead. " +
                              MemberChecks.suppressedBy( Constants.WARNING_JSR_330_NAMED ) );
      warning( message, element );
    }
    if ( AnnotationsUtil.hasAnnotationOfType( element, Constants.CDI_TYPED_CLASSNAME ) &&
         ElementsUtil.isWarningNotSuppressed( element, Constants.WARNING_CDI_TYPED ) )
    {
      final String message =
        MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                              "be annotated with the " + Constants.CDI_TYPED_CLASSNAME + " annotation. " +
                              "Use the " + Constants.TYPED_CLASSNAME + " annotation instead. " +
                              MemberChecks.suppressedBy( Constants.WARNING_CDI_TYPED ) );
      warning( message, element );
    }
    if ( AnnotationsUtil.hasAnnotationOfType( constructor, Constants.JSR_330_INJECT_CLASSNAME ) &&
         ElementsUtil.isWarningNotSuppressed( constructor, Constants.WARNING_JSR_330_INJECT ) )
    {
      final String message =
        MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                              "be annotated with the " + Constants.JSR_330_INJECT_CLASSNAME + " annotation. " +
                              MemberChecks.suppressedBy( Constants.WARNING_JSR_330_INJECT ) );
      warning( message, constructor );
    }
    @SuppressWarnings( "unchecked" )
    final List<TypeMirror> types =
      null == value ?
      Collections.singletonList( element.asType() ) :
      ( (List<AnnotationValue>) value.getValue() )
        .stream()
        .map( v -> (TypeMirror) v.getValue() )
        .toList();

    final ServiceSpec[] specs = new ServiceSpec[ types.size() ];
    for ( int i = 0; i < specs.length; i++ )
    {
      final TypeMirror type = types.get( i );
      if ( !processingEnv.getTypeUtils().isAssignable( element.asType(), type ) )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( Constants.TYPED_CLASSNAME ) +
                                      " specified a type that is not assignable to the declaring type",
                                      element,
                                      annotation,
                                      value );
      }
      else if ( TypeKind.DECLARED == type.getKind() && isParameterized( (DeclaredType) type ) )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( Constants.TYPED_CLASSNAME ) +
                                      " specified a type that is a a parameterized type",
                                      element,
                                      annotation,
                                      value );
      }
      specs[ i ] = new ServiceSpec( new Coordinate( qualifier, type ), false );
    }

    if ( 0 == specs.length && !eager )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                          "specify zero types with the " +
                                                          MemberChecks.toSimpleName( Constants.TYPED_CLASSNAME ) +
                                                          " annotation or must be annotated with the " +
                                                          MemberChecks.toSimpleName( Constants.EAGER_CLASSNAME ) +
                                                          " annotation otherwise the component can not be created by the injector" ),
                                    element );
    }
    if ( 0 == specs.length && !qualifier.isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                          "specify zero types with the " +
                                                          MemberChecks.toSimpleName( Constants.TYPED_CLASSNAME ) +
                                                          " annotation and specify a qualifier with the " +
                                                          MemberChecks.toSimpleName( Constants.NAMED_CLASSNAME ) +
                                                          " annotation as the qualifier is meaningless" ),
                                    element );
    }

    final Binding binding =
      new Binding( Binding.Kind.INJECTABLE,
                   element.getQualifiedName().toString(),
                   Arrays.asList( specs ),
                   eager,
                   constructor,
                   dependencies.toArray( new ServiceRequest[ 0 ] ) );
    final InjectableDescriptor injectable = new InjectableDescriptor( binding );
    _registry.registerInjectable( injectable );
  }

  @Nonnull
  private FactoryMethodDescriptor processFactoryMethod( @Nonnull final TypeElement factory,
                                                        @Nonnull final ExecutableElement method,
                                                        @Nonnull final List<FactoryDependencyDescriptor> dependencies,
                                                        @Nonnull final Set<String> usedFieldNames )
  {
    if ( TypeKind.VOID == method.getReturnType().getKind() )
    {
      throw new ProcessorException( MemberChecks.must( Constants.FACTORY_CLASSNAME,
                                                       "only contain abstract methods that return a value" ),
                                    method );
    }
    else if ( !method.getTypeParameters().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FACTORY_CLASSNAME,
                                                          "contain abstract methods with a type parameter" ),
                                    method );
    }
    else if ( !method.getThrownTypes().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FACTORY_CLASSNAME,
                                                          "contain abstract methods that throw exceptions" ),
                                    method );
    }
    else if ( TypeKind.DECLARED != method.getReturnType().getKind() )
    {
      throw new ProcessorException( MemberChecks.must( Constants.FACTORY_CLASSNAME,
                                                       "contain abstract methods that return a class type" ),
                                    method );
    }

    final DeclaredType returnType = (DeclaredType) method.getReturnType();
    if ( !returnType.getTypeArguments().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FACTORY_CLASSNAME,
                                                          "contain abstract methods that return a parameterized type" ),
                                    method );
    }

    final TypeElement producedType = (TypeElement) returnType.asElement();
    if ( ElementKind.CLASS != producedType.getKind() )
    {
      throw new ProcessorException( MemberChecks.must( Constants.FACTORY_CLASSNAME,
                                                       "contain abstract methods that return a class type" ),
                                    method );
    }
    else if ( producedType.getModifiers().contains( Modifier.ABSTRACT ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FACTORY_CLASSNAME,
                                                          "contain abstract methods that return an abstract class" ),
                                    method );
    }
    else if ( ElementsUtil.isNonStaticNestedClass( producedType ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FACTORY_CLASSNAME,
                                                          "contain abstract methods that return a non-static nested class" ),
                                    method );
    }
    else if ( !producedType.getTypeParameters().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FACTORY_CLASSNAME,
                                                          "contain abstract methods that return a class with type parameters" ),
                                    method );
    }

    final List<ExecutableElement> constructors = ElementsUtil.getConstructors( producedType );
    if ( 1 != constructors.size() )
    {
      throw new ProcessorException( MemberChecks.must( Constants.FACTORY_CLASSNAME,
                                                       "create types with a single accessible constructor" ),
                                    method );
    }
    final ExecutableElement constructor = constructors.get( 0 );
    if ( !isFactoryAccessible( factory, constructor ) )
    {
      throw new ProcessorException( MemberChecks.must( Constants.FACTORY_CLASSNAME,
                                                       "create types with a constructor accessible from the factory interface" ),
                                    method );
    }

    final List<? extends VariableElement> constructorParameters = constructor.getParameters();
    final List<? extends VariableElement> methodParameters = method.getParameters();
    final Map<Integer, VariableElement> methodParametersByConstructorIndex = new LinkedHashMap<>();
    final Map<Integer, FactoryDependencyDescriptor> dependenciesByConstructorIndex = new LinkedHashMap<>();
    final List<? extends TypeMirror> constructorParameterTypes =
      ( (ExecutableType) constructor.asType() ).getParameterTypes();

    for ( final VariableElement parameter : methodParameters )
    {
      boolean matched = false;
      for ( int i = 0; i < constructorParameters.size(); i++ )
      {
        final VariableElement constructorParameter = constructorParameters.get( i );
        if ( constructorParameter.getSimpleName().contentEquals( parameter.getSimpleName() ) )
        {
          matched = true;
          if ( methodParametersByConstructorIndex.containsKey( i ) )
          {
            throw new ProcessorException( MemberChecks.mustNot( Constants.FACTORY_CLASSNAME,
                                                                "contain duplicate abstract method parameters that " +
                                                                "match the same constructor parameter" ),
                                          parameter );
          }
          if ( !processingEnv.getTypeUtils().isSameType( parameter.asType(), constructorParameterTypes.get( i ) ) )
          {
            throw new ProcessorException( MemberChecks.must( Constants.FACTORY_CLASSNAME,
                                                             "contain abstract method parameters whose name and type " +
                                                             "match the created type constructor parameter" ),
                                          parameter );
          }
          methodParametersByConstructorIndex.put( i, parameter );
          break;
        }
      }
      if ( !matched )
      {
        throw new ProcessorException( MemberChecks.must( Constants.FACTORY_CLASSNAME,
                                                         "contain abstract method parameters whose name and type " +
                                                         "match the created type constructor parameter" ),
                                      parameter );
      }
    }

    for ( int i = 0; i < constructorParameters.size(); i++ )
    {
      if ( !methodParametersByConstructorIndex.containsKey( i ) )
      {
        final VariableElement constructorParameter = constructorParameters.get( i );
        final ServiceRequest request = handleConstructorParameter( constructorParameter, constructorParameterTypes.get( i ), i );
        final FactoryDependencyDescriptor dependency =
          findOrCreateFactoryDependency( request, dependencies, usedFieldNames );
        dependenciesByConstructorIndex.put( i, dependency );
      }
    }

    return new FactoryMethodDescriptor( method,
                                        producedType.asType(),
                                        constructor,
                                        constructorParameters,
                                        methodParametersByConstructorIndex,
                                        dependenciesByConstructorIndex );
  }

  @Nonnull
  private FactoryDependencyDescriptor findOrCreateFactoryDependency( @Nonnull final ServiceRequest request,
                                                                     @Nonnull final List<FactoryDependencyDescriptor> dependencies,
                                                                     @Nonnull final Set<String> usedFieldNames )
  {
    for ( final FactoryDependencyDescriptor existing : dependencies )
    {
      if ( existing.matches( request ) )
      {
        return existing;
      }
    }

    final VariableElement parameter = (VariableElement) request.getElement();
    final String parameterName = parameter.getSimpleName().toString();
    final String fieldName = uniqueFactoryFieldName( parameterName, usedFieldNames );
    final FactoryDependencyDescriptor dependency =
      new FactoryDependencyDescriptor( request, parameterName, fieldName );
    dependencies.add( dependency );
    return dependency;
  }

  @Nonnull
  private String uniqueFactoryFieldName( @Nonnull final String parameterName, @Nonnull final Set<String> usedFieldNames )
  {
    final String baseName = StingGeneratorUtil.FRAMEWORK_PREFIX + parameterName;
    String candidate = baseName;
    int index = 2;
    while ( !usedFieldNames.add( candidate ) )
    {
      candidate = baseName + index;
      index++;
    }
    return candidate;
  }

  private boolean isFactoryAccessible( @Nonnull final TypeElement factory, @Nonnull final ExecutableElement constructor )
  {
    final Set<Modifier> modifiers = constructor.getModifiers();
    if ( modifiers.contains( Modifier.PRIVATE ) )
    {
      return false;
    }
    else if ( modifiers.contains( Modifier.PUBLIC ) )
    {
      return true;
    }
    else
    {
      final String factoryPackage = GeneratorUtil.getQualifiedPackageName( factory );
      final String targetPackage =
        GeneratorUtil.getQualifiedPackageName( (TypeElement) constructor.getEnclosingElement() );
      return factoryPackage.equals( targetPackage );
    }
  }

  // Binary descriptor writing and verification removed

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
  private ServiceRequest handleConstructorParameter( @Nonnull final VariableElement parameter,
                                                     @Nonnull final TypeMirror type,
                                                     final int parameterIndex )
  {
    if ( TypesUtil.containsArrayType( type ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                          "contain a constructor with a parameter that contains an array type" ),
                                    parameter );
    }
    else if ( TypesUtil.containsWildcard( type ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                          "contain a constructor with a parameter that contains a wildcard type parameter" ),
                                    parameter );
    }
    else if ( TypesUtil.containsRawType( type ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                          "contain a constructor with a parameter that contains a raw type" ),
                                    parameter );
    }
    else
    {
      TypeMirror dependencyType = null;
      ServiceRequest.Kind kind = null;
      for ( final ServiceRequest.Kind candidate : ServiceRequest.Kind.values() )
      {
        dependencyType = candidate.extractType( type );
        if ( null != dependencyType )
        {
          kind = candidate;
          break;
        }
      }
      if ( null == kind )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                            "contain a constructor with a parameter that contains an unexpected parameterized type. Only parameterized types known to the framework are supported" ),
                                      parameter );
      }
      else
      {
        final boolean optional = AnnotationsUtil.hasNullableAnnotation( parameter );
        if ( optional && ServiceRequest.Kind.INSTANCE != kind )
        {
          throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                              "contain a constructor with a parameter annotated with " +
                                                              MemberChecks.toSimpleName( AnnotationsUtil.NULLABLE_CLASSNAME ) +
                                                              " that is not an instance dependency kind" ),
                                        parameter );
        }
        final String qualifier = getQualifier( parameter );
        if ( AnnotationsUtil.hasAnnotationOfType( parameter, Constants.JSR_330_NAMED_CLASSNAME ) &&
             ElementsUtil.isWarningNotSuppressed( parameter, Constants.WARNING_JSR_330_NAMED ) )
        {
          final String message =
            MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                  "contain a constructor with a parameter annotated with the " +
                                  Constants.JSR_330_NAMED_CLASSNAME + " annotation. Use the " +
                                  Constants.NAMED_CLASSNAME + " annotation instead. " +
                                  MemberChecks.suppressedBy( Constants.WARNING_JSR_330_NAMED ) );
          warning( message, parameter );
        }
        final Coordinate coordinate = new Coordinate( qualifier, dependencyType );
        final ServiceSpec service = new ServiceSpec( coordinate, optional );
        return new ServiceRequest( kind, service, parameter, parameterIndex );
      }
    }
  }

  @Nonnull
  private String getQualifier( @Nonnull final Element element )
  {
    final AnnotationMirror annotation = AnnotationsUtil.findAnnotationByType( element, Constants.NAMED_CLASSNAME );
    return null == annotation ? "" : AnnotationsUtil.getAnnotationValueValue( annotation, "value" );
  }

  private void injectableShouldNotHaveScopedAnnotation( @Nonnull final TypeElement element )
  {
    final List<? extends AnnotationMirror> scopedAnnotations = getScopedAnnotations( element );
    if ( !scopedAnnotations.isEmpty() &&
         ElementsUtil.isWarningNotSuppressed( element, Constants.WARNING_JSR_330_SCOPED ) )
    {
      final String message =
        MemberChecks.shouldNot( Constants.INJECTABLE_CLASSNAME,
                                "be annotated with an annotation that is annotated with the " +
                                Constants.JSR_330_SCOPE_CLASSNAME + " annotation such as " + scopedAnnotations + ". " +
                                MemberChecks.suppressedBy( Constants.WARNING_JSR_330_SCOPED ) );
      warning( message, element );
    }
  }

  private void injectableConstructorShouldNotBePublic( @Nonnull final ExecutableElement constructor )
  {
    if ( Elements.Origin.EXPLICIT == processingEnv.getElementUtils().getOrigin( constructor ) &&
         constructor.getModifiers().contains( Modifier.PUBLIC ) &&
         ElementsUtil.isWarningNotSuppressed( constructor, Constants.WARNING_PUBLIC_CONSTRUCTOR ) )
    {
      final String message =
        MemberChecks.shouldNot( Constants.INJECTABLE_CLASSNAME,
                                "have a public constructor. The type is instantiated by the injector " +
                                "and should have a package-access constructor. " +
                                MemberChecks.suppressedBy( Constants.WARNING_PUBLIC_CONSTRUCTOR ) );
      warning( message, constructor );
    }
  }

  private void injectableConstructorShouldNotBeProtected( @Nonnull final ExecutableElement constructor )
  {
    if ( constructor.getModifiers().contains( Modifier.PROTECTED ) &&
         ElementsUtil.isWarningNotSuppressed( constructor, Constants.WARNING_PROTECTED_CONSTRUCTOR ) )
    {
      final String message =
        MemberChecks.shouldNot( Constants.INJECTABLE_CLASSNAME,
                                "have a protected constructor. The type is instantiated by the " +
                                "injector and should have a package-access constructor. " +
                                MemberChecks.suppressedBy( Constants.WARNING_PROTECTED_CONSTRUCTOR ) );
      warning( message, constructor );
    }
  }

  private boolean isParameterized( @Nonnull final DeclaredType nestedParameterType )
  {
    return !( (TypeElement) nestedParameterType.asElement() ).getTypeParameters().isEmpty();
  }

  @Nonnull
  private List<? extends AnnotationMirror> getScopedAnnotations( @Nonnull final Element element )
  {
    return element
      .getAnnotationMirrors()
      .stream()
      .filter( a -> AnnotationsUtil.hasAnnotationOfType( a.getAnnotationType().asElement(),
                                                         Constants.JSR_330_SCOPE_CLASSNAME ) )
      .collect( Collectors.toList() );
  }

  @Nullable
  private FragmentDescriptor deriveFragmentDescriptor( @Nonnull final TypeElement element )
  {
    final String classname = element.getQualifiedName().toString();
    final FragmentDescriptor cached = _derivedFragmentCache.get( classname );
    if ( null != cached )
    {
      return cached;
    }
    // Only derive for proper @Fragment types
    else if ( ElementKind.INTERFACE != element.getKind() ||
              !AnnotationsUtil.hasAnnotationOfType( element, Constants.FRAGMENT_CLASSNAME ) )
    {
      return null;
    }
    else if ( !SuperficialValidation.validateElement( processingEnv, element ) )
    {
      return null;
    }
    else
    {
      final boolean localOnly = extractFragmentLocalOnly( element );
      final List<IncludeDescriptor> includes = extractIncludes( element, Constants.FRAGMENT_CLASSNAME, false );
      final Map<ExecutableElement, Binding> bindings = new LinkedHashMap<>();
      for ( final Element enclosedElement : element.getEnclosedElements() )
      {
        if ( ElementKind.METHOD == enclosedElement.getKind() )
        {
          processProvidesMethod( element, bindings, (ExecutableElement) enclosedElement );
        }
      }
      final FragmentDescriptor fragment = new FragmentDescriptor( element, includes, localOnly, bindings.values() );
      fragment.markJavaStubAsGenerated();
      _derivedFragmentCache.put( classname, fragment );
      return fragment;
    }
  }

  private boolean extractFragmentLocalOnly( @Nonnull final TypeElement element )
  {
    return (boolean) AnnotationsUtil.getAnnotationValue( element, Constants.FRAGMENT_CLASSNAME, "localOnly" )
      .getValue();
  }

  private boolean isInSamePackage( @Nonnull final TypeElement type1, @Nonnull final TypeElement type2 )
  {
    return GeneratorUtil.getQualifiedPackageName( type1 ).equals( GeneratorUtil.getQualifiedPackageName( type2 ) );
  }

  @Nullable
  private InjectableDescriptor deriveInjectableDescriptor( @Nonnull final TypeElement element )
  {
    final String classname = element.getQualifiedName().toString();
    final InjectableDescriptor cached = _derivedInjectableCache.get( classname );
    if ( null != cached )
    {
      return cached;
    }
    // Only derive for proper @Injectable types
    if ( ElementKind.CLASS != element.getKind() ||
         !AnnotationsUtil.hasAnnotationOfType( element, Constants.INJECTABLE_CLASSNAME ) )
    {
      return null;
    }

    if ( element.getModifiers().contains( Modifier.ABSTRACT ) ||
         ElementsUtil.isNonStaticNestedClass( element ) ||
         !element.getTypeParameters().isEmpty() )
    {
      // Invalid injectable; let normal processing flag this if encountered as source.
      return null;
    }

    final List<ExecutableElement> constructors = ElementsUtil.getConstructors( element );
    if ( constructors.isEmpty() )
    {
      return null;
    }
    final ExecutableElement constructor = constructors.get( 0 );
    if ( constructors.size() > 1 )
    {
      return null;
    }

    final boolean eager = AnnotationsUtil.hasAnnotationOfType( element, Constants.EAGER_CLASSNAME );

    final List<ServiceRequest> dependencies = new ArrayList<>();
    int index = 0;
    final List<? extends TypeMirror> parameterTypes = ( (ExecutableType) constructor.asType() ).getParameterTypes();
    for ( final VariableElement parameter : constructor.getParameters() )
    {
      dependencies.add( handleConstructorParameter( parameter, parameterTypes.get( index ), index ) );
      index++;
    }

    final AnnotationMirror annotation =
      AnnotationsUtil.findAnnotationByType( element, Constants.TYPED_CLASSNAME );
    final AnnotationValue value =
      null != annotation ? AnnotationsUtil.findAnnotationValue( annotation, "value" ) : null;

    final String qualifier = getQualifier( element );

    @SuppressWarnings( "unchecked" )
    final List<TypeMirror> types =
      null == value ?
      Collections.singletonList( element.asType() ) :
      ( (List<AnnotationValue>) value.getValue() )
        .stream()
        .map( v -> (TypeMirror) v.getValue() )
        .toList();

    final ServiceSpec[] specs = new ServiceSpec[ types.size() ];
    for ( int i = 0; i < specs.length; i++ )
    {
      final TypeMirror type = types.get( i );
      if ( !processingEnv.getTypeUtils().isAssignable( element.asType(), type ) )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( Constants.TYPED_CLASSNAME ) +
                                      " specified a type that is not assignable to the declaring type",
                                      element,
                                      annotation,
                                      value );
      }
      else if ( TypeKind.DECLARED == type.getKind() && isParameterized( (DeclaredType) type ) )
      {
        throw new ProcessorException( MemberChecks.toSimpleName( Constants.TYPED_CLASSNAME ) +
                                      " specified a type that is a a parameterized type",
                                      element,
                                      annotation,
                                      value );
      }
      specs[ i ] = new ServiceSpec( new Coordinate( qualifier, type ), false );
    }

    if ( 0 == specs.length && !eager )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                          "specify zero types with the " +
                                                          MemberChecks.toSimpleName( Constants.TYPED_CLASSNAME ) +
                                                          " annotation or must be annotated with the " +
                                                          MemberChecks.toSimpleName( Constants.EAGER_CLASSNAME ) +
                                                          " annotation otherwise the component can not be created by the injector" ),
                                    element );
    }
    if ( 0 == specs.length && !qualifier.isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                          "specify zero types with the " +
                                                          MemberChecks.toSimpleName( Constants.TYPED_CLASSNAME ) +
                                                          " annotation and specify a qualifier with the " +
                                                          MemberChecks.toSimpleName( Constants.NAMED_CLASSNAME ) +
                                                          " annotation as the qualifier is meaningless" ),
                                    element );
    }

    final Binding binding =
      new Binding( Binding.Kind.INJECTABLE,
                   element.getQualifiedName().toString(),
                   Arrays.asList( specs ),
                   eager,
                   constructor,
                   dependencies.toArray( new ServiceRequest[ 0 ] ) );
    final InjectableDescriptor injectable = new InjectableDescriptor( binding );
    injectable.markJavaStubAsGenerated();
    _derivedInjectableCache.put( classname, injectable );
    return injectable;
  }

  private void maybeWarnOnRedundantDirectInjectableInclude( @Nonnull final TypeElement originator,
                                                            @Nonnull final String annotationClassname,
                                                            @Nonnull final Collection<IncludeDescriptor> includes )
  {
    if ( !ElementsUtil.isWarningNotSuppressed( originator,
                                               Constants.WARNING_REDUNDANT_DIRECT_INJECTABLE_INCLUDE ) )
    {
      return;
    }

    final Set<String> directInjectables = new HashSet<>();
    final Set<String> transitiveInjectables = new HashSet<>();

    for ( final IncludeDescriptor include : includes )
    {
      final String classname = include.getActualTypeName();
      final TypeElement element = processingEnv.getElementUtils().getTypeElement( classname );
      if ( null == element )
      {
        continue;
      }
      if ( AnnotationsUtil.hasAnnotationOfType( element, Constants.INJECTABLE_CLASSNAME ) )
      {
        // Only consider explicit direct includes as candidates (for Injector, auto-includes are not considered direct)
        if ( !include.isAuto() )
        {
          directInjectables.add( classname );
        }
      }
      else if ( AnnotationsUtil.hasAnnotationOfType( element, Constants.FRAGMENT_CLASSNAME ) )
      {
        collectTransitiveInjectablesFromFragment( classname, transitiveInjectables, new HashSet<>() );
      }
    }

    directInjectables.retainAll( transitiveInjectables );
    if ( !directInjectables.isEmpty() )
    {
      for ( final String classname : directInjectables )
      {
        final String message =
          MemberChecks.shouldNot( annotationClassname,
                                  "include type " + classname +
                                  " as it is already transitively included via included fragments. " +
                                  MemberChecks.suppressedBy( Constants.WARNING_REDUNDANT_DIRECT_INJECTABLE_INCLUDE ) );
        warning( message, originator );
      }
    }
  }

  private void collectTransitiveInjectablesFromFragment( @Nonnull final String fragmentClassname,
                                                         @Nonnull final Set<String> collector,
                                                         @Nonnull final Set<String> visitedFragments )
  {
    if ( !visitedFragments.add( fragmentClassname ) )
    {
      return;
    }
    final FragmentDescriptor fragment = _registry.findFragmentByClassName( fragmentClassname );
    if ( null == fragment )
    {
      return;
    }
    for ( final IncludeDescriptor include : fragment.getIncludes() )
    {
      final String classname = include.getActualTypeName();
      final TypeElement element = processingEnv.getElementUtils().getTypeElement( classname );
      if ( null == element )
      {
        continue;
      }
      if ( AnnotationsUtil.hasAnnotationOfType( element, Constants.INJECTABLE_CLASSNAME ) )
      {
        collector.add( classname );
      }
      else if ( AnnotationsUtil.hasAnnotationOfType( element, Constants.FRAGMENT_CLASSNAME ) )
      {
        collectTransitiveInjectablesFromFragment( classname, collector, visitedFragments );
      }
    }
  }

  private void maybeWarnOnFragmentIncludeCycle( @Nonnull final TypeElement originator,
                                                @Nonnull final Collection<IncludeDescriptor> includes )
  {
    if ( !ElementsUtil.isWarningNotSuppressed( originator, Constants.WARNING_FRAGMENT_INCLUDE_CYCLE ) )
    {
      return;
    }
    final String originClassname = originator.getQualifiedName().toString();
    for ( final IncludeDescriptor include : includes )
    {
      final String classname = include.getActualTypeName();
      final TypeElement element = processingEnv.getElementUtils().getTypeElement( classname );
      if ( null == element )
      {
        continue;
      }
      if ( AnnotationsUtil.hasAnnotationOfType( element, Constants.FRAGMENT_CLASSNAME ) )
      {
        if ( fragmentTransitivelyIncludes( classname, originClassname, new HashSet<>() ) )
        {
          final String message =
            MemberChecks.shouldNot( Constants.FRAGMENT_CLASSNAME,
                                    "include a fragment " + classname +
                                    " that transitively includes " + originClassname + ". " +
                                    MemberChecks.suppressedBy( Constants.WARNING_FRAGMENT_INCLUDE_CYCLE ) );
          warning( message, originator );
          return;
        }
      }
    }
  }

  private boolean fragmentTransitivelyIncludes( @Nonnull final String fragmentClassname,
                                                @Nonnull final String targetFragmentClassname,
                                                @Nonnull final Set<String> visited )
  {
    if ( !visited.add( fragmentClassname ) )
    {
      return false;
    }
    if ( fragmentClassname.equals( targetFragmentClassname ) )
    {
      return true;
    }
    final FragmentDescriptor fragment = _registry.findFragmentByClassName( fragmentClassname );
    if ( null == fragment )
    {
      return false;
    }
    for ( final IncludeDescriptor include : fragment.getIncludes() )
    {
      final String classname = include.getActualTypeName();
      final TypeElement element = processingEnv.getElementUtils().getTypeElement( classname );
      if ( null != element && AnnotationsUtil.hasAnnotationOfType( element, Constants.FRAGMENT_CLASSNAME ) )
      {
        if ( fragmentTransitivelyIncludes( classname, targetFragmentClassname, visited ) )
        {
          return true;
        }
      }
    }
    return false;
  }
}
