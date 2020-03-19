package sting.processor;

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
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
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
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import org.realityforge.proton.AbstractStandardProcessor;
import org.realityforge.proton.AnnotationsUtil;
import org.realityforge.proton.DeferredElementSet;
import org.realityforge.proton.ElementsUtil;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.IOUtil;
import org.realityforge.proton.JsonUtil;
import org.realityforge.proton.MemberChecks;
import org.realityforge.proton.ProcessorException;
import org.realityforge.proton.ResourceUtil;
import org.realityforge.proton.SuperficialValidation;
import org.realityforge.proton.TypesUtil;

/**
 * The annotation processor that analyzes Sting annotated source code and generates an injector and supporting artifacts.
 */
@SuppressWarnings( "DuplicatedCode" )
@SupportedAnnotationTypes( { Constants.INJECTOR_CLASSNAME,
                             Constants.INJECTABLE_CLASSNAME,
                             Constants.FRAGMENT_CLASSNAME,
                             Constants.EAGER_CLASSNAME,
                             Constants.TYPED_CLASSNAME,
                             Constants.NAMED_CLASSNAME,
                             Constants.AUTO_FRAGMENT_CLASSNAME,
                             Constants.CONTRIBUTE_TO_CLASSNAME } )
@SupportedSourceVersion( SourceVersion.RELEASE_8 )
@SupportedOptions( { "sting.defer.unresolved",
                     "sting.defer.errors",
                     "sting.debug",
                     "sting.emit_json_descriptors",
                     "sting.emit_dot_reports",
                     "sting.verbose_out_of_round.errors",
                     "sting.verify_descriptors" } )
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
  @Nonnull
  private final DeferredElementSet _deferredInjectableTypes = new DeferredElementSet();
  @Nonnull
  private final DeferredElementSet _deferredFragmentTypes = new DeferredElementSet();
  @Nonnull
  private final DeferredElementSet _deferredInjectorTypes = new DeferredElementSet();
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
  /**
   * Flag controlling whether .dot formatted report is emitted.
   * The .dot report is typically used by end users who want to explore the graph.
   */
  private boolean _emitDotReports;

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
  public synchronized void init( final ProcessingEnvironment processingEnv )
  {
    super.init( processingEnv );
    _descriptorIO = new DescriptorIO( processingEnv.getElementUtils(), processingEnv.getTypeUtils() );
    _emitJsonDescriptors = readBooleanOption( "emit_json_descriptors", false );
    _emitDotReports = readBooleanOption( "emit_dot_reports", false );
    _verifyDescriptors = readBooleanOption( "verify_descriptors", false );
  }

  @Override
  public boolean process( @Nonnull final Set<? extends TypeElement> annotations, @Nonnull final RoundEnvironment env )
  {
    // Reset modified flag for auto-fragment so we can determine
    // whether we should generate fragment this round
    _registry.getAutoFragments().forEach( AutoFragmentDescriptor::resetModified );

    processAutoFragments( annotations, env );

    processContributeTos( annotations, env );

    processTypeElements( annotations,
                         env,
                         Constants.INJECTABLE_CLASSNAME,
                         _deferredInjectableTypes,
                         this::processInjectable );

    processTypeElements( annotations,
                         env,
                         Constants.FRAGMENT_CLASSNAME,
                         _deferredFragmentTypes,
                         this::processFragment );

    annotations.stream()
      .filter( a -> a.getQualifiedName().toString().equals( Constants.NAMED_CLASSNAME ) )
      .findAny()
      .ifPresent( a -> verifyNamedElements( env, env.getElementsAnnotatedWith( a ) ) );

    annotations.stream()
      .filter( a -> a.getQualifiedName().toString().equals( Constants.TYPED_CLASSNAME ) )
      .findAny()
      .ifPresent( a -> verifyTypedElements( env, env.getElementsAnnotatedWith( a ) ) );

    annotations.stream()
      .filter( a -> a.getQualifiedName().toString().equals( Constants.EAGER_CLASSNAME ) )
      .findAny()
      .ifPresent( a -> verifyEagerElements( env, env.getElementsAnnotatedWith( a ) ) );

    processTypeElements( annotations,
                         env,
                         Constants.INJECTOR_CLASSNAME,
                         _deferredInjectorTypes,
                         this::processInjector );

    processUnmodifiedAutoFragment( env );
    processResolvedInjectables( env );
    processResolvedFragments( env );
    processResolvedInjectors( env );

    errorIfProcessingOverAndInvalidTypesDetected( env );
    errorIfProcessingOverAndUnprocessedInjectorDetected( env );
    errorIfProcessingOverAndUnprocessedAutoFragmentsDetected( env );
    errorIfProcessingOverAndUnprocessedContributeTosDetected( env );
    if ( env.processingOver() || env.errorRaised() )
    {
      _registry.clear();
    }
    return true;
  }

  private void processAutoFragments( @Nonnull final Set<? extends TypeElement> annotations,
                                     @Nonnull final RoundEnvironment env )
  {
    final Collection<TypeElement> autoFragments =
      getNewTypeElementsToProcess( annotations, env, Constants.AUTO_FRAGMENT_CLASSNAME );
    for ( final TypeElement element : autoFragments )
    {
      performAction( env, this::processAutoFragment, element );
    }
  }

  private void processContributeTos( @Nonnull final Set<? extends TypeElement> annotations,
                                     @Nonnull final RoundEnvironment env )
  {
    final Collection<TypeElement> contributeTos =
      getNewTypeElementsToProcess( annotations, env, Constants.CONTRIBUTE_TO_CLASSNAME );
    for ( final TypeElement element : contributeTos )
    {
      performAction( env, this::processContributeTo, element );
    }
  }

  private void errorIfProcessingOverAndUnprocessedAutoFragmentsDetected( @Nonnull final RoundEnvironment env )
  {
    if ( env.processingOver() && !env.errorRaised() )
    {
      final Collection<AutoFragmentDescriptor> autoFragments =
        _registry.getAutoFragments().stream().filter( a -> !a.isFragmentGenerated() ).collect( Collectors.toList() );
      if ( !autoFragments.isEmpty() )
      {
        processingEnv
          .getMessager()
          .printMessage( Diagnostic.Kind.ERROR,
                         getClass().getSimpleName() + " failed to process " + autoFragments.size() +
                         " @AutoFragment annotated types as the fragments either contained no contributors " +
                         "or only had contributors added in the last annotation processor round which is not " +
                         "supported by the @AutoFragment annotation. If the problem is not obvious, consider " +
                         "passing the annotation option sting.debug=true" );
        for ( final AutoFragmentDescriptor autoFragment : autoFragments )
        {
          processingEnv
            .getMessager()
            .printMessage( Diagnostic.Kind.ERROR,
                           "Failed to process the " + autoFragment.getElement().getQualifiedName() +
                           " @AutoFragment as 0 @ContributeTo annotations reference the @AutoFragment" );
        }
      }
    }
  }

  private void errorIfProcessingOverAndUnprocessedContributeTosDetected( @Nonnull final RoundEnvironment env )
  {
    if ( env.processingOver() && !env.errorRaised() )
    {
      final Collection<String> contributorKeys =
        _registry.getContributorKeys()
          .stream()
          .filter( key -> null == _registry.findAutoFragmentByKey( key ) )
          .collect( Collectors.toList() );
      if ( !contributorKeys.isEmpty() )
      {
        for ( final String contributorKey : contributorKeys )
        {
          processingEnv
            .getMessager()
            .printMessage( Diagnostic.Kind.ERROR,
                           "Failed to process the @ContributeTo contributors for key '" + contributorKey +
                           "' as no associated @AutoFragment is on the class path. Impacted contributors included: " +
                           _registry.getContributorsByKey( contributorKey ).stream()
                             .map( TypeElement::getQualifiedName )
                             .collect( Collectors.joining( ", " ) ) );
        }
      }
    }
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
                         "as not all of their dependencies could be resolved. The java code resolved but the " +
                         "descriptors were missing or in the incorrect format. Ensure that the included " +
                         "typed have been compiled with a compatible version of Sting and that the .sbf " +
                         "descriptors have been packaged with the .class files. If the problem is not " +
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

  private void processUnmodifiedAutoFragment( @Nonnull final RoundEnvironment env )
  {
    for ( final AutoFragmentDescriptor autoFragment : new ArrayList<>( _registry.getAutoFragments() ) )
    {
      performAction( env, e -> {
        if ( !autoFragment.isFragmentGenerated() )
        {
          if ( !autoFragment.isModified() &&
               !autoFragment.getContributors().isEmpty() )
          {
            autoFragment.markFragmentGenerated();
            final String packageName = GeneratorUtil.getQualifiedPackageName( autoFragment.getElement() );
            emitTypeSpec( packageName, AutoFragmentGenerator.buildType( processingEnv, autoFragment ) );
          }
          else
          {
            debug( () -> "Defer generation for the auto-fragment " +
                         autoFragment.getElement().getQualifiedName() +
                         " as it " +
                         ( autoFragment.isModified() ?
                           "has been modified in the current round" :
                           "has zero contributors" ) );
          }
        }
      }, autoFragment.getElement() );
    }
  }

  private void processResolvedInjectables( @Nonnull final RoundEnvironment env )
  {
    for ( final InjectableDescriptor injectable : new ArrayList<>( _registry.getInjectables() ) )
    {
      performAction( env, e -> {
        if ( !injectable.isJavaStubGenerated() )
        {
          injectable.markJavaStubAsGenerated();
          writeBinaryDescriptor( injectable.getElement(), injectable );
          emitInjectableJsonDescriptor( injectable );
          emitInjectableStub( injectable );
        }
      }, injectable.getElement() );
    }
  }

  private void emitInjectableStub( @Nonnull final InjectableDescriptor injectable )
    throws IOException
  {
    final String packageName = GeneratorUtil.getQualifiedPackageName( injectable.getElement() );
    emitTypeSpec( packageName, InjectableGenerator.buildType( processingEnv, injectable ) );
  }

  private void processResolvedFragments( @Nonnull final RoundEnvironment env )
  {
    for ( final FragmentDescriptor fragment : new ArrayList<>( _registry.getFragments() ) )
    {
      performAction( env, e -> {
        if ( !fragment.isJavaStubGenerated() && isFragmentReady( env, fragment ) )
        {
          fragment.markJavaStubAsGenerated();
          writeBinaryDescriptor( fragment.getElement(), fragment );
          emitFragmentJsonDescriptor( fragment );
          emitFragmentStub( fragment );
        }
      }, fragment.getElement() );
    }
  }

  private boolean isFragmentReady( @Nonnull final RoundEnvironment env,
                                   @Nonnull final FragmentDescriptor fragment )
  {
    return !fragment.containsError() && isFragmentResolved( env, fragment );
  }

  private void emitFragmentStub( @Nonnull final FragmentDescriptor fragment )
    throws IOException
  {
    final String packageName = GeneratorUtil.getQualifiedPackageName( fragment.getElement() );
    emitTypeSpec( packageName, FragmentGenerator.buildType( processingEnv, fragment ) );
  }

  private void processResolvedInjectors( @Nonnull final RoundEnvironment env )
  {
    for ( final InjectorDescriptor injector : new ArrayList<>( _registry.getInjectors() ) )
    {
      performAction( env, e -> {
        if ( !injector.containsError() )
        {
          if ( isInjectorResolved( env, injector ) )
          {
            _registry.deregisterInjector( injector );
            buildAndEmitObjectGraph( injector );
          }
          else
          {
            debug( () -> "Defer generation for the injector " + injector.getElement().getQualifiedName() +
                         " as it is not yet resolved" );
          }
        }
      }, injector.getElement() );
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
    registerIncludes( graph, graph.getInjector().getIncludes() );
  }

  private void registerIncludes( @Nonnull final ComponentGraph graph,
                                 @Nonnull final Collection<IncludeDescriptor> includes )
  {
    for ( final IncludeDescriptor include : includes )
    {
      final String classname = include.getActualTypeName();
      final TypeElement element = processingEnv.getElementUtils().getTypeElement( classname );
      assert null != element;
      if ( AnnotationsUtil.hasAnnotationOfType( element, Constants.FRAGMENT_CLASSNAME ) )
      {
        final FragmentDescriptor fragment = _registry.getFragmentByClassName( element.getQualifiedName().toString() );
        registerIncludes( graph, fragment.getIncludes() );
        graph.registerFragment( fragment );
      }
      else
      {
        assert AnnotationsUtil.hasAnnotationOfType( element, Constants.INJECTABLE_CLASSNAME );
        graph.registerInjectable( _registry.getInjectableByClassName( element.getQualifiedName().toString() ) );
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
    final List<Node> eagerNodes = new ArrayList<>( graph.getRawNodeCollection() );
    final Node rootNode = graph.getRootNode();
    rootNode.setDepth( 0 );
    addDependsOnToWorkList( workList, rootNode, null );
    processWorkList( graph, completed, workList );
    for ( final Node node : eagerNodes )
    {
      if ( node.isDepthNotSet() )
      {
        node.setDepth( 0 );
        addDependsOnToWorkList( workList, node, null );
        processWorkList( graph, completed, workList );
      }
    }
    graph.complete();
  }

  private void processWorkList( @Nonnull final ComponentGraph graph,
                                @Nonnull final Set<Node> completed,
                                @Nonnull final Stack<WorkEntry> workList )
  {
    final InjectorDescriptor injector = graph.getInjector();
    while ( !workList.isEmpty() )
    {
      final WorkEntry workEntry = workList.pop();
      final Edge edge = workEntry.getEntry().getEdge();
      assert null != edge;
      final ServiceRequest serviceRequest = edge.getServiceRequest();
      final Coordinate coordinate = serviceRequest.getService().getCoordinate();
      final List<Binding> bindings = new ArrayList<>( graph.findAllBindingsByCoordinate( coordinate ) );

      if ( bindings.isEmpty() )
      {
        final String classname = coordinate.getType().toString();
        final InjectableDescriptor injectable = _registry.findInjectableByClassName( classname );
        if ( null != injectable &&
             injectable.getBinding()
               .getPublishedServices()
               .stream()
               .anyMatch( s -> coordinate.equals( s.getCoordinate() ) ) )
        {
          bindings.add( injectable.getBinding() );
        }
        if ( bindings.isEmpty() )
        {
          final TypeElement typeElement = processingEnv.getElementUtils().getTypeElement( classname );
          final byte[] data = tryLoadDescriptorData( typeElement );
          if ( null != data )
          {
            final Node node = edge.getNode();
            final Object owner = node.hasNoBinding() ? null : node.getBinding().getOwner();
            final TypeElement ownerElement =
              owner instanceof FragmentDescriptor ? ( (FragmentDescriptor) owner ).getElement() :
              owner instanceof InjectableDescriptor ? ( (InjectableDescriptor) owner ).getElement() :
              injector.getElement();

            final Object descriptor = loadDescriptor( ownerElement, classname, data );
            if ( descriptor instanceof InjectableDescriptor )
            {
              final InjectableDescriptor injectableDescriptor = (InjectableDescriptor) descriptor;
              if ( injectableDescriptor.getBinding()
                .getPublishedServices()
                .stream()
                .anyMatch( s -> coordinate.equals( s.getCoordinate() ) ) )
              {
                _registry.registerInjectable( injectableDescriptor );
                bindings.add( injectableDescriptor.getBinding() );
              }
            }
          }
        }
      }

      final List<Binding> nullableProviders = bindings.stream()
        .filter( b -> b.getPublishedServices().stream().anyMatch( ServiceSpec::isOptional ) )
        .collect( Collectors.toList() );
      if ( !serviceRequest.getService().isOptional() && !nullableProviders.isEmpty() )
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
        if ( serviceRequest.getService().isOptional() || serviceRequest.getKind().isCollection() )
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

  private boolean isFragmentResolved( @Nonnull final RoundEnvironment env, @Nonnull final FragmentDescriptor fragment )
  {
    if ( fragment.isResolved() )
    {
      return true;
    }
    else if ( isResolved( env, fragment, fragment.getElement(), fragment.getIncludes() ) )
    {
      fragment.markAsResolved();
      return true;
    }
    else
    {
      return false;
    }
  }

  private boolean isInjectorResolved( @Nonnull final RoundEnvironment env, @Nonnull final InjectorDescriptor injector )
  {
    return isResolved( env, injector, injector.getElement(), injector.getIncludes() );
  }

  private boolean isResolved( @Nonnull final RoundEnvironment env,
                              @Nonnull final Object descriptor,
                              @Nonnull final TypeElement originator,
                              @Nonnull final Collection<IncludeDescriptor> includes )
  {
    boolean resolved = true;
    // By the time we get here we can guarantee that the java types are correctly resolved
    // so we only have to check that the descriptors are present and valid in this method
    // Except for providers. Providers may be loaded a lot later
    for ( final IncludeDescriptor include : includes )
    {
      final String classname = include.getActualTypeName();
      final TypeElement element = processingEnv.getElementUtils().getTypeElement( classname );
      if ( null == element )
      {
        assert include.isProvider();
        if ( env.processingOver() )
        {
          AnnotationMirror annotation =
            AnnotationsUtil.findAnnotationByType( originator, Constants.INJECTOR_CLASSNAME );

          final String annotationClassname =
            null != annotation ? Constants.INJECTOR_CLASSNAME : Constants.FRAGMENT_CLASSNAME;
          if ( null == annotation )
          {
            annotation = AnnotationsUtil.getAnnotationByType( originator, Constants.FRAGMENT_CLASSNAME );
          }

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
            "type need to be removed from the includes or the provider class needs to be present.";
          reportError( env, message, originator, annotation, null );
        }
        return false;
      }
      final boolean isInjectable = AnnotationsUtil.hasAnnotationOfType( element, Constants.INJECTABLE_CLASSNAME );
      final boolean isFragment =
        !isInjectable && AnnotationsUtil.hasAnnotationOfType( element, Constants.FRAGMENT_CLASSNAME );
      if ( include.isProvider() && !isInjectable && !isFragment )
      {
        AnnotationMirror annotation =
          AnnotationsUtil.findAnnotationByType( originator, Constants.INJECTOR_CLASSNAME );

        final String annotationClassname =
          null != annotation ? Constants.INJECTOR_CLASSNAME : Constants.FRAGMENT_CLASSNAME;
        if ( null == annotation )
        {
          annotation = AnnotationsUtil.getAnnotationByType( originator, Constants.FRAGMENT_CLASSNAME );
        }
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
          MemberChecks.toSimpleName( Constants.INJECTOR_CLASSNAME ) + " or " +
          MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME );
        throw new ProcessorException( message, originator, annotation );
      }
      if ( isFragment )
      {
        FragmentDescriptor fragment = _registry.findFragmentByClassName( classname );
        if ( null == fragment )
        {
          final byte[] data = tryLoadDescriptorData( element );
          if ( null == data )
          {
            debug( () -> "The fragment " + classname + " is compiled to a .class file but no descriptor is present. " +
                         "Marking " + originator.getQualifiedName() + " as unresolved" );
            return false;
          }
          final Object loadedDescriptor = loadDescriptor( originator, classname, data );
          if ( loadedDescriptor instanceof FragmentDescriptor )
          {
            fragment = (FragmentDescriptor) loadedDescriptor;
            _registry.registerFragment( fragment );
          }
          else
          {
            debug( () -> "The fragment " + classname + " is compiled to a .class " +
                         "file but an invalid descriptor is present. " +
                         "Marking " + originator.getQualifiedName() + " as unresolved" );
            return false;
          }
        }
        if ( !isFragmentReady( env, fragment ) )
        {
          debug( () -> "Fragment include " + classname + " is present but not yet resolved. " +
                       "Marking " + originator.getQualifiedName() + " as unresolved" );
          resolved = false;
        }
      }
      else
      {
        InjectableDescriptor injectable = _registry.findInjectableByClassName( classname );
        if ( null == injectable )
        {
          final byte[] data = tryLoadDescriptorData( element );
          if ( null == data )
          {
            debug( () -> "The injectable " + classname + " is compiled to a .class file but no descriptor is present." +
                         "Marking " + originator.getQualifiedName() + " as unresolved" );
            return false;
          }
          final Object loadedDescriptor = loadDescriptor( originator, classname, data );
          if ( loadedDescriptor instanceof InjectableDescriptor )
          {
            injectable = (InjectableDescriptor) loadedDescriptor;
            _registry.registerInjectable( injectable );
          }
          else
          {
            debug( () -> "The injectable " + classname + " is compiled to a .class " +
                         "file but an invalid descriptor is present. " +
                         "Marking " + originator.getQualifiedName() + " as unresolved" );
            return false;
          }
        }
        if ( !SuperficialValidation.validateElement( processingEnv, injectable.getElement() ) )
        {
          debug( () -> "Injectable include " + classname + " is not yet resolved. " +
                       "Marking " + originator.getQualifiedName() + " as unresolved" );
          resolved = false;
        }
      }
    }
    return resolved;
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
    final List<IncludeDescriptor> includes = extractIncludes( element, Constants.INJECTOR_CLASSNAME );
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
    }
    for ( final Element enclosedElement : element.getEnclosedElements() )
    {
      if ( ElementKind.INTERFACE == enclosedElement.getKind() &&
           AnnotationsUtil.hasAnnotationOfType( enclosedElement, Constants.FRAGMENT_CLASSNAME ) )
      {
        final DeclaredType type = (DeclaredType) enclosedElement.asType();
        if ( includes.stream().noneMatch( d -> Objects.equals( d.getIncludedType(), type ) ) )
        {
          includes.add( new IncludeDescriptor( type, type.toString() ) );
        }
      }
      else if ( ElementKind.CLASS == enclosedElement.getKind() &&
                AnnotationsUtil.hasAnnotationOfType( enclosedElement, Constants.INJECTABLE_CLASSNAME ) )
      {
        final DeclaredType type = (DeclaredType) enclosedElement.asType();
        if ( includes.stream().noneMatch( d -> Objects.equals( d.getIncludedType(), type ) ) )
        {
          includes.add( new IncludeDescriptor( type, type.toString() ) );
        }
      }
    }
    final InjectorDescriptor injector = new InjectorDescriptor( element, gwt, injectable, includes, inputs, outputs );
    _registry.registerInjector( injector );
    emitInjectorJsonDescriptor( injector );
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
        if ( !injectableType && ElementKind.CONSTRUCTOR == executableKind && !isProvider )
        {
          reportError( env,
                       MemberChecks.must( Constants.NAMED_CLASSNAME,
                                          "only be present on a constructor parameter if the constructor " +
                                          "is enclosed in a type annotated with " +
                                          MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME ) +
                                          " or the type has an associated provider" ),
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
                 ( isFragmentType && ElementKind.METHOD == executableKind );
        }
      }
      else if ( ElementKind.CLASS == element.getKind() )
      {
        if ( !AnnotationsUtil.hasAnnotationOfType( element, Constants.INJECTABLE_CLASSNAME ) &&
             !hasStingProvider( element ) )
        {
          reportError( env,
                       MemberChecks.must( Constants.NAMED_CLASSNAME,
                                          "only be present on a type if the type is annotated with " +
                                          MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME ) +
                                          " or the type has an associated provider" ),
                       element );
        }
      }
      else if ( ElementKind.METHOD == element.getKind() )
      {
        if ( !AnnotationsUtil.hasAnnotationOfType( element.getEnclosingElement(), Constants.FRAGMENT_CLASSNAME ) &&
             !AnnotationsUtil.hasAnnotationOfType( element.getEnclosingElement(), Constants.INJECTOR_CLASSNAME ) )
        {
          reportError( env,
                       MemberChecks.mustNot( Constants.NAMED_CLASSNAME,
                                             "be a method unless the method is enclosed in a type annotated with " +
                                             MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME ) + " or " +
                                             MemberChecks.toSimpleName( Constants.INJECTOR_CLASSNAME ) ),
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
    return element
      .getAnnotationMirrors()
      .stream()
      .anyMatch( a -> a.getAnnotationType()
        .asElement()
        .getAnnotationMirrors()
        .stream()
        .anyMatch( ca -> ca.getAnnotationType().asElement().getSimpleName().contentEquals( "StingProvider" ) ) );
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
                                          " or the type has an associated provider" ),
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
                                          " or the type has an associated provider" ),
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
    final List<IncludeDescriptor> includes = extractIncludes( element, Constants.FRAGMENT_CLASSNAME );
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
    final List<? extends AnnotationMirror> scopedAnnotations = getScopedAnnotations( element );
    if ( !scopedAnnotations.isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                          "be annotated with an annotation that is " +
                                                          "annotated with the " + Constants.JSR_330_SCOPE_CLASSNAME +
                                                          " annotation such as " + scopedAnnotations ),
                                    element );
    }
    _registry.registerFragment( new FragmentDescriptor( element, includes, bindings.values() ) );
  }

  private void processAutoFragment( @Nonnull final TypeElement element )
  {
    debug( () -> "Processing Auto-Fragment: " + element );
    if ( ElementKind.INTERFACE != element.getKind() )
    {
      throw new ProcessorException( MemberChecks.must( Constants.AUTO_FRAGMENT_CLASSNAME, "be an interface" ),
                                    element );
    }
    else if ( !element.getTypeParameters().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.AUTO_FRAGMENT_CLASSNAME, "have type parameters" ),
                                    element );
    }
    else if ( !element.getInterfaces().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.AUTO_FRAGMENT_CLASSNAME, "extend any interfaces" ),
                                    element );
    }
    if ( !element.getEnclosedElements().isEmpty() )
    {
      final Element enclosedElement = element.getEnclosedElements().get( 0 );
      final ElementKind kind = enclosedElement.getKind();
      if ( kind.isField() )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.AUTO_FRAGMENT_CLASSNAME, "contain any fields" ),
                                      element );
      }
      else if ( ElementKind.METHOD == kind )
      {
        throw new ProcessorException( MemberChecks.mustNot( Constants.AUTO_FRAGMENT_CLASSNAME, "contain any methods" ),
                                      element );
      }
      else
      {
        assert kind.isClass() || kind.isInterface();
        throw new ProcessorException( MemberChecks.mustNot( Constants.AUTO_FRAGMENT_CLASSNAME, "contain any types" ),
                                      element );
      }
    }
    final String key = (String)
      AnnotationsUtil.getAnnotationValue( element, Constants.AUTO_FRAGMENT_CLASSNAME, "value" ).getValue();

    final AutoFragmentDescriptor existing = _registry.findAutoFragmentByKey( key );
    if ( null != existing )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.AUTO_FRAGMENT_CLASSNAME,
                                                          "have the same key as an existing AutoFragment " +
                                                          "of type " + existing.getElement().getQualifiedName() ),
                                    element );
    }

    _registry.registerAutoFragment( new AutoFragmentDescriptor( key, element ) );
  }

  private void processContributeTo( @Nonnull final TypeElement element )
  {
    debug( () -> "Processing ContributeTo: " + element );
    if ( !AnnotationsUtil.hasAnnotationOfType( element, Constants.FRAGMENT_CLASSNAME ) &&
         !AnnotationsUtil.hasAnnotationOfType( element, Constants.INJECTABLE_CLASSNAME ) &&
         !hasStingProvider( element ) )
    {
      throw new ProcessorException( MemberChecks.must( Constants.CONTRIBUTE_TO_CLASSNAME,
                                                       "be annotated with " +
                                                       MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME ) +
                                                       ", " +
                                                       MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME ) +
                                                       " or be annotated with an annotation annotated by " +
                                                       "@StingProvider" ),
                                    element );
    }
    final String key = (String)
      AnnotationsUtil.getAnnotationValue( element, Constants.CONTRIBUTE_TO_CLASSNAME, "value" ).getValue();

    final AutoFragmentDescriptor autoFragment = _registry.findAutoFragmentByKey( key );
    if ( null != autoFragment && autoFragment.isFragmentGenerated() )
    {
      throw new ProcessorException( MemberChecks.toSimpleName( Constants.CONTRIBUTE_TO_CLASSNAME ) +
                                    " target attempted to be added to the " +
                                    MemberChecks.toSimpleName( Constants.AUTO_FRAGMENT_CLASSNAME ) +
                                    " annotated type " + autoFragment.getElement().getQualifiedName() +
                                    " but the " + MemberChecks.toSimpleName( Constants.AUTO_FRAGMENT_CLASSNAME ) +
                                    " annotated type has already generated fragment",
                                    element );
    }

    _registry.registerContributor( key, element );
    if ( null != autoFragment )
    {
      autoFragment.markAsModified();
      autoFragment.getContributors().add( element );
    }
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
                                                   @Nonnull final String annotationClassname )
  {
    final List<IncludeDescriptor> results = new ArrayList<>();
    final List<TypeMirror> includes =
      AnnotationsUtil.getTypeMirrorsAnnotationParameter( element, annotationClassname, "includes" );
    for ( final TypeMirror include : includes )
    {
      final Element includeElement = processingEnv.getTypeUtils().asElement( include );
      if ( AnnotationsUtil.hasAnnotationOfType( includeElement, Constants.FRAGMENT_CLASSNAME ) ||
           AnnotationsUtil.hasAnnotationOfType( includeElement, Constants.INJECTABLE_CLASSNAME ) )
      {
        results.add( new IncludeDescriptor( (DeclaredType) include, include.toString() ) );
      }
      else
      {
        final ElementKind kind = includeElement.getKind();
        if ( ElementKind.CLASS == kind || ElementKind.INTERFACE == kind )
        {
          final List<ProviderEntry> providers =
            includeElement.getAnnotationMirrors()
              .stream()
              .map( a -> {
                final AnnotationMirror provider =
                  getStingProvider( element, annotationClassname, (TypeElement) includeElement, a );
                return null != provider ? new ProviderEntry( a, provider ) : null;
              } )
              .filter( Objects::nonNull )
              .collect( Collectors.toList() );
          if ( providers.size() > 1 )
          {
            final String message =
              MemberChecks.toSimpleName( annotationClassname ) + " target has an " +
              "'includes' parameter containing the value " + includeElement.asType() +
              " that is annotated by multiple @StingProvider annotations. Matching annotations:\n" +
              providers
                .stream()
                .map( a -> ( (TypeElement) a.getAnnotation().getAnnotationType().asElement() ).getQualifiedName() )
                .map( a -> "  " + a )
                .collect( Collectors.joining( "\n" ) );
            throw new ProcessorException( message, element );
          }
          else if ( !providers.isEmpty() )
          {
            final ProviderEntry entry = providers.get( 0 );
            final AnnotationMirror providerAnnotation = entry.getProvider();
            final String namePattern = AnnotationsUtil.getAnnotationValueValue( providerAnnotation, "value" );

            final String targetCompoundType =
              namePattern
                .replace( "[SimpleName]", includeElement.getSimpleName().toString() )
                .replace( "[CompoundName]", getComponentName( (TypeElement) includeElement ) )
                .replace( "[EnclosingName]", getEnclosingName( (TypeElement) includeElement ) )
                .replace( "[FlatEnclosingName]", getEnclosingName( (TypeElement) includeElement ).replace( '.', '_' ) );

            final String targetQualifiedName =
              ElementsUtil.getPackageElement( includeElement ).getQualifiedName().toString() + "." + targetCompoundType;

            results.add( new IncludeDescriptor( (DeclaredType) include, targetQualifiedName ) );
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
    }
    return results;
  }

  @Nullable
  private AnnotationMirror getStingProvider( @Nonnull final TypeElement element,
                                             @Nonnull final String annotationClassname,
                                             @Nonnull final TypeElement includeElement,
                                             @Nonnull final AnnotationMirror annotation )
  {
    return annotation.getAnnotationType()
      .asElement()
      .getAnnotationMirrors()
      .stream()
      .filter( ca -> isStingProvider( element, annotationClassname, includeElement, ca ) )
      .findAny()
      .orElse( null );
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
                                   @Nonnull final String annotationClassname,
                                   @Nonnull final Element includeElement,
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
        final String message =
          MemberChecks.toSimpleName( annotationClassname ) + " target has an " +
          "'includes' parameter containing the value " + includeElement.asType() +
          " that is annotated by " + annotation + " that is annotated by an invalid @StingProvider " +
          "annotation missing a 'value' parameter of type string.";
        throw new ProcessorException( message, element );
      }
    }
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
        .collect( Collectors.toList() );

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
    if ( 0 == specs.length && !"".equals( qualifier ) )
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
      processingEnv.getMessager().printMessage( Diagnostic.Kind.WARNING, message, element );
    }
    if ( AnnotationsUtil.hasAnnotationOfType( element, Constants.CDI_TYPED_CLASSNAME ) &&
         ElementsUtil.isWarningNotSuppressed( element, Constants.WARNING_CDI_TYPED ) )
    {
      final String message =
        MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                              "be annotated with the " + Constants.CDI_TYPED_CLASSNAME + " annotation. " +
                              "Use the " + Constants.TYPED_CLASSNAME + " annotation instead. " +
                              MemberChecks.suppressedBy( Constants.WARNING_CDI_TYPED ) );
      processingEnv.getMessager().printMessage( Diagnostic.Kind.WARNING, message, element );
    }
    if ( AnnotationsUtil.hasAnnotationOfType( constructor, Constants.JSR_330_INJECT_CLASSNAME ) &&
         ElementsUtil.isWarningNotSuppressed( constructor, Constants.WARNING_JSR_330_INJECT ) )
    {
      final String message =
        MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                              "be annotated with the " + Constants.JSR_330_INJECT_CLASSNAME + " annotation. " +
                              MemberChecks.suppressedBy( Constants.WARNING_JSR_330_INJECT ) );
      processingEnv.getMessager().printMessage( Diagnostic.Kind.WARNING, message, constructor );
    }
    @SuppressWarnings( "unchecked" )
    final List<TypeMirror> types =
      null == value ?
      Collections.singletonList( element.asType() ) :
      ( (List<AnnotationValue>) value.getValue() )
        .stream()
        .map( v -> (TypeMirror) v.getValue() )
        .collect( Collectors.toList() );

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
    if ( 0 == specs.length && !"".equals( qualifier ) )
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
      try ( final DataOutputStream dos = new DataOutputStream( out ) )
      {
        _descriptorIO.write( dos, descriptor );
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
                                    ". Reading the emitted descriptor did not produce an equivalent descriptor.",
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
          processingEnv.getMessager().printMessage( Diagnostic.Kind.WARNING, message, parameter );
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
      processingEnv.getMessager().printMessage( Diagnostic.Kind.WARNING, message, element );
    }
  }

  private void injectableConstructorShouldNotBePublic( @Nonnull final ExecutableElement constructor )
  {
    if ( ElementsUtil.isNotSynthetic( constructor ) &&
         constructor.getModifiers().contains( Modifier.PUBLIC ) &&
         ElementsUtil.isWarningNotSuppressed( constructor, Constants.WARNING_PUBLIC_CONSTRUCTOR ) )
    {
      final String message =
        MemberChecks.shouldNot( Constants.INJECTABLE_CLASSNAME,
                                "have a public constructor. The type is instantiated by the injector " +
                                "and should have a package-access constructor. " +
                                MemberChecks.suppressedBy( Constants.WARNING_PUBLIC_CONSTRUCTOR ) );
      processingEnv.getMessager().printMessage( Diagnostic.Kind.WARNING, message, constructor );
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
      processingEnv.getMessager().printMessage( Diagnostic.Kind.WARNING, message, constructor );
    }
  }

  @Nonnull
  private Object loadDescriptor( @Nonnull final Element originator,
                                 @Nonnull final String classname,
                                 @Nonnull final byte[] data )
  {
    try
    {
      return _descriptorIO.read( new DataInputStream( new ByteArrayInputStream( data ) ), classname );
    }
    catch ( final IOException e )
    {
      throw new ProcessorException( "Failed to read the Sting descriptor for the type " + classname + ". Error: " + e,
                                    originator );
    }
  }

  @Nullable
  private byte[] tryLoadDescriptorData( @Nonnull final TypeElement element )
  {
    byte[] data = tryLoadDescriptorData( StandardLocation.CLASS_PATH, element );
    data = null != data ? data : tryLoadDescriptorData( StandardLocation.CLASS_OUTPUT, element );
    // Some tools (IDEA?) will actually put dependencies on the boot/platform class path. This
    // seems like it should be an error but as long as the tools do this, the annotation processor
    // must also be capable of loading descriptor data from the platform classpath
    return null != data ? data : tryLoadDescriptorData( StandardLocation.PLATFORM_CLASS_PATH, element );
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
}
