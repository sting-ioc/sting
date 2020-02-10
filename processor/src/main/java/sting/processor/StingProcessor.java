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
import org.realityforge.proton.ElementsUtil;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.IOUtil;
import org.realityforge.proton.JsonUtil;
import org.realityforge.proton.MemberChecks;
import org.realityforge.proton.ProcessorException;

/**
 * Annotation processor that analyzes Sting annotated source code and generates source to support the Sting elements.
 */
@SuppressWarnings( "DuplicatedCode" )
@SupportedAnnotationTypes( { Constants.INJECTOR_CLASSNAME,
                             Constants.INJECTABLE_CLASSNAME,
                             Constants.FRAGMENT_CLASSNAME,
                             Constants.EAGER_CLASSNAME,
                             Constants.TYPED_CLASSNAME,
                             Constants.NAMED_CLASSNAME,
                             Constants.INPUT_CLASSNAME } )
@SupportedSourceVersion( SourceVersion.RELEASE_8 )
@SupportedOptions( { "sting.defer.unresolved",
                     "sting.defer.errors",
                     "sting.emit_json_descriptors",
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

    annotations.stream()
      .filter( a -> a.getQualifiedName().toString().equals( Constants.INJECTOR_CLASSNAME ) )
      .findAny()
      .ifPresent( a -> processTypeElements( env,
                                            (Collection<TypeElement>) env.getElementsAnnotatedWith( a ),
                                            this::processInjector ) );

    processResolvedInjectables( env );
    processResolvedFragments( env );
    processResolvedInjectors( env );

    errorIfProcessingOverAndInvalidTypesDetected( env );
    errorIfProcessingOverAndDeferredTypesUnprocessed( env );
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
                         "as not all of their dependencies could be resolved. The java code resolved but the " +
                         "descriptors were missing or in the incorrect format. Ensure that the included " +
                         "typed have been compiled with a compatible version of Sting and that the .sbf " +
                         "descriptors have been packaged with the .class files." );
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
      performAction( env, e -> {
        if ( isInjectableResolved( injectable ) && !injectable.isJavaStubGenerated() )
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
        if ( isFragmentResolved( fragment ) && !fragment.isJavaStubGenerated() )
        {
          fragment.markJavaStubAsGenerated();
          writeBinaryDescriptor( fragment.getElement(), fragment );
          emitFragmentJsonDescriptor( fragment );
          emitFragmentStub( fragment );
        }
      }, fragment.getElement() );
    }
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
        if ( isInjectorResolved( injector ) )
        {
          _registry.deregisterInjector( injector );
          buildAndEmitObjectGraph( injector );
        }
      }, injector.getElement() );
    }
  }

  private void buildAndEmitObjectGraph( @Nonnull final InjectorDescriptor injector )
    throws Exception
  {
    final ComponentGraph graph = new ComponentGraph( injector );
    registerIncludesComponents( graph );

    registerInputs( graph );

    buildObjectGraphNodes( graph );

    if ( graph.getNodes().isEmpty() && graph.getRootNode().getDependsOn().isEmpty() )
    {
      throw new ProcessorException( MemberChecks.toSimpleName( Constants.INJECTOR_CLASSNAME ) + " target " +
                                    "produced an empty object graph. This means that there are no eager nodes " +
                                    "in the includes and there are no dependencies or only unsatisfied optional " +
                                    "dependencies defined by the injector",
                                    graph.getInjector().getElement() );
    }

    propagateEagerFlagUpstream( graph );

    CircularDependencyChecker.verifyNoCircularDependencyLoops( graph );

    emitObjectGraphJsonDescriptor( graph );

    final String packageName = GeneratorUtil.getQualifiedPackageName( graph.getInjector().getElement() );
    emitTypeSpec( packageName, InjectorGenerator.buildType( processingEnv, graph ) );
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
        graph.registerInjectable( injectable );
      }
    }
  }

  private void buildObjectGraphNodes( @Nonnull final ComponentGraph graph )
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
      final ServiceDescriptor service = edge.getService();
      final Coordinate coordinate = service.getService().getCoordinate();
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
            try
            {
              final Object descriptor = loadDescriptor( classname, data );
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
        .filter( b -> b.getPublishedServices().stream().anyMatch( ServiceSpec::isOptional ) )
        .collect( Collectors.toList() );
      if ( !service.getService().isOptional() && !nullableProviders.isEmpty() )
      {
        final String message =
          MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                "contain an optional provider method or optional injector input and " +
                                "a non-optional service request for the coordinate " + coordinate + "\n" +
                                "Dependency Path:\n" + workEntry.describePathFromRoot() + "\n" +
                                "Binding" + ( nullableProviders.size() > 1 ? "s" : "" ) + ":\n" +
                                bindingsToString( nullableProviders ) );
        throw new ProcessorException( message, service.getElement() );
      }
      if ( bindings.isEmpty() )
      {
        if ( service.getService().isOptional() || service.getKind().isCollection() )
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
          throw new ProcessorException( message, service.getElement() );
        }
      }
      else
      {
        final ServiceDescriptor.Kind kind = service.getKind();
        if ( 1 == bindings.size() || kind.isCollection() )
        {
          final List<Node> nodes = new ArrayList<>();
          for ( final Binding binding : bindings )
          {
            final String id = binding.getId();
            final Node existing = graph.findNodeById( id );
            if ( null != existing && binding != existing.getBinding() )
            {
              throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                                                  "contain multiple nodes with the id '" +
                                                                  id + "'.\nPath:\n" +
                                                                  workEntry.describePathFromRoot() ),
                                            existing.getBinding().getElement().getEnclosingElement() );
            }
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
          throw new ProcessorException( message, service.getElement() );
        }
      }
    }
    graph.complete();
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
      JsonUtil.writeJsonResource( processingEnv, element, filename, graph::write );
    }
  }

  private boolean isInjectableResolved( @Nonnull final InjectableDescriptor injectable )
  {
    return isResolved( injectable.getElement(), Collections.emptyList() );
  }

  private boolean isFragmentResolved( @Nonnull final FragmentDescriptor fragment )
  {
    return isResolved( fragment.getElement(), fragment.getIncludes() );
  }

  private boolean isInjectorResolved( @Nonnull final InjectorDescriptor injector )
  {
    return isResolved( injector.getElement(), injector.getIncludes() );
  }

  private boolean isResolved( @Nonnull final TypeElement originator, @Nonnull final Collection<DeclaredType> includes )
  {
    // By the time we get here we can guarantee that the java types are correctly resolved
    // so we only have to check that the descriptors are present and valid in this method
    for ( final DeclaredType include : includes )
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
                                        originator );
        }
      }
    }
    return true;
  }

  private void processInjector( @Nonnull final TypeElement element )
    throws Exception
  {
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

    final List<DeclaredType> includes = extractIncludes( element, Constants.INJECTOR_CLASSNAME );
    final List<InputDescriptor> inputs = extractInputs( element );

    final List<ServiceDescriptor> outputs = new ArrayList<>();
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
      if ( enclosedElement.getKind() == ElementKind.INTERFACE &&
           AnnotationsUtil.hasAnnotationOfType( enclosedElement, Constants.FRAGMENT_CLASSNAME ) )
      {
        final DeclaredType type = (DeclaredType) enclosedElement.asType();
        if ( !includes.contains( type ) )
        {
          includes.add( type );
        }
      }
      else if ( enclosedElement.getKind() == ElementKind.CLASS &&
                AnnotationsUtil.hasAnnotationOfType( enclosedElement, Constants.INJECTABLE_CLASSNAME ) )
      {
        final DeclaredType type = (DeclaredType) enclosedElement.asType();
        if ( !includes.contains( type ) )
        {
          includes.add( type );
        }
      }
    }
    final InjectorDescriptor injector = new InjectorDescriptor( element, includes, inputs, outputs );
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

  private void processInjectorOutputMethod( @Nonnull final List<ServiceDescriptor> outputs,
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
    MemberChecks.mustNotHaveAnyTypeParameters( Constants.INPUT_CLASSNAME, method );
    outputs.add( processOutputMethod( method ) );
  }

  @Nonnull
  private ServiceDescriptor processOutputMethod( @Nonnull final ExecutableElement method )
  {
    final TypeMirror type = method.getReturnType();
    if ( StingTypesUtil.containsArrayType( type ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                                          "contain a method with a return type that contains an array type" ),
                                    method );
    }
    else if ( StingTypesUtil.containsWildcard( type ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                                          "contain a method with a return type that contains a wildcard type parameter" ),
                                    method );
    }
    else if ( StingTypesUtil.containsRawType( type ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                                          "contain a method with a return type that contains a raw type" ),
                                    method );
    }
    else
    {
      TypeMirror dependencyType = null;
      ServiceDescriptor.Kind kind = null;
      for ( final ServiceDescriptor.Kind candidate : ServiceDescriptor.Kind.values() )
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
        final boolean optional =
          AnnotationsUtil.hasAnnotationOfType( method, GeneratorUtil.NULLABLE_ANNOTATION_CLASSNAME );
        if ( optional && ServiceDescriptor.Kind.INSTANCE != kind )
        {
          throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTOR_CLASSNAME,
                                                              "contain a method annotated with " +
                                                              MemberChecks.toSimpleName( GeneratorUtil.NULLABLE_ANNOTATION_CLASSNAME ) +
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
        return new ServiceDescriptor( kind, service, method, -1 );
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
          AnnotationsUtil.hasAnnotationOfType( executableElement.getEnclosingElement(), Constants.FRAGMENT_CLASSNAME );
        final ElementKind executableKind = executableElement.getKind();
        if ( !injectableType && ElementKind.CONSTRUCTOR == executableKind )
        {
          reportError( env,
                       MemberChecks.must( Constants.NAMED_CLASSNAME,
                                          "only be present on a constructor parameter if the constructor is enclosed in a type annotated with " +
                                          MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME ) ),
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
                 ( isFragmentType && ElementKind.METHOD == executableKind );
        }
      }
      else if ( ElementKind.CLASS == element.getKind() )
      {
        if ( !AnnotationsUtil.hasAnnotationOfType( element, Constants.INJECTABLE_CLASSNAME ) )
        {
          reportError( env,
                       MemberChecks.must( Constants.NAMED_CLASSNAME,
                                          "only be present on a type if the type is annotated with " +
                                          MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME ) ),
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

  private void verifyTypedElements( @Nonnull final RoundEnvironment env,
                                    @Nonnull final Set<? extends Element> elements )
  {
    for ( final Element element : elements )
    {
      if ( ElementKind.CLASS == element.getKind() )
      {
        if ( !AnnotationsUtil.hasAnnotationOfType( element, Constants.INJECTABLE_CLASSNAME ) )
        {
          reportError( env,
                       MemberChecks.must( Constants.TYPED_CLASSNAME,
                                          "only be present on a type if the type is annotated with " +
                                          MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME ) ),
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
        if ( !AnnotationsUtil.hasAnnotationOfType( element, Constants.INJECTABLE_CLASSNAME ) )
        {
          reportError( env,
                       MemberChecks.must( Constants.EAGER_CLASSNAME,
                                          "only be present on a type if the type is annotated with " +
                                          MemberChecks.toSimpleName( Constants.INJECTABLE_CLASSNAME ) ),
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
      final AnnotationValue typeAnnotationValue = AnnotationsUtil._getAnnotationValue( input, "type" );
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
                     new ServiceDescriptor[ 0 ] );
      results.add( new InputDescriptor( service, binding, "input" + ( i + 1 ) ) );
    }
    return results;
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
    final boolean nullablePresent =
      AnnotationsUtil.hasAnnotationOfType( method, GeneratorUtil.NULLABLE_ANNOTATION_CLASSNAME );

    if ( nullablePresent && method.getReturnType().getKind().isPrimitive() )
    {
      throw new ProcessorException( MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME ) +
                                    " contains a method that is incorrectly annotated with " +
                                    MemberChecks.toSimpleName( GeneratorUtil.NULLABLE_ANNOTATION_CLASSNAME ) +
                                    " as the return type is a primitive value",
                                    method );
    }

    final boolean eager = AnnotationsUtil.hasAnnotationOfType( method, Constants.EAGER_CLASSNAME );

    final List<ServiceDescriptor> dependencies = new ArrayList<>();
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
      throw new ProcessorException( MemberChecks.toSimpleName( Constants.FRAGMENT_CLASSNAME ) +
                                    " target must not contain methods that specify zero types with the " +
                                    MemberChecks.toSimpleName( Constants.TYPED_CLASSNAME ) +
                                    " annotation and are not annotated with the " +
                                    MemberChecks.toSimpleName( Constants.EAGER_CLASSNAME ) +
                                    " annotation otherwise the component can not be created by the injector",
                                    element );
    }
    final Binding binding =
      new Binding( Binding.Kind.PROVIDES,
                   element.getQualifiedName() + "#" + method.getSimpleName(),
                   Arrays.asList( specs ),
                   eager,
                   method,
                   dependencies.toArray( new ServiceDescriptor[ 0 ] ) );
    bindings.put( method, binding );
  }

  @Nonnull
  private ServiceDescriptor processFragmentServiceParameter( @Nonnull final VariableElement parameter,
                                                             @Nonnull final TypeMirror type,
                                                             final int parameterIndex )
  {
    if ( StingTypesUtil.containsArrayType( type ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                          "contain a method with a parameter that contains an array type" ),
                                    parameter );
    }
    else if ( StingTypesUtil.containsWildcard( type ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                          "contain a method with a parameter that contains a wildcard type parameter" ),
                                    parameter );
    }
    else if ( StingTypesUtil.containsRawType( type ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                          "contain a method with a parameter that contains a raw type" ),
                                    parameter );
    }
    else
    {
      TypeMirror dependencyType = null;
      ServiceDescriptor.Kind kind = null;
      for ( final ServiceDescriptor.Kind candidate : ServiceDescriptor.Kind.values() )
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
        final boolean optional =
          AnnotationsUtil.hasAnnotationOfType( parameter, GeneratorUtil.NULLABLE_ANNOTATION_CLASSNAME );
        if ( optional && ServiceDescriptor.Kind.INSTANCE != kind )
        {
          throw new ProcessorException( MemberChecks.mustNot( Constants.FRAGMENT_CLASSNAME,
                                                              "contain a method with a parameter annotated with the " +
                                                              MemberChecks.toSimpleName( GeneratorUtil.NULLABLE_ANNOTATION_CLASSNAME ) +
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
        return new ServiceDescriptor( kind, service, parameter, parameterIndex );
      }
    }
  }

  private void processInjectable( @Nonnull final TypeElement element )
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

    final boolean eager = AnnotationsUtil.hasAnnotationOfType( element, Constants.EAGER_CLASSNAME );

    final List<ServiceDescriptor> dependencies = new ArrayList<>();
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

    final Binding binding =
      new Binding( Binding.Kind.INJECTABLE,
                   element.getQualifiedName().toString(),
                   Arrays.asList( specs ),
                   eager,
                   constructor,
                   dependencies.toArray( new ServiceDescriptor[ 0 ] ) );
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
  private ServiceDescriptor handleConstructorParameter( @Nonnull final VariableElement parameter,
                                                        @Nonnull final TypeMirror type,
                                                        final int parameterIndex )
  {
    if ( StingTypesUtil.containsArrayType( type ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                          "contain a constructor with a parameter that contains an array type" ),
                                    parameter );
    }
    else if ( StingTypesUtil.containsWildcard( type ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                          "contain a constructor with a parameter that contains a wildcard type parameter" ),
                                    parameter );
    }
    else if ( StingTypesUtil.containsRawType( type ) )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                          "contain a constructor with a parameter that contains a raw type" ),
                                    parameter );
    }
    else
    {
      TypeMirror dependencyType = null;
      ServiceDescriptor.Kind kind = null;
      for ( final ServiceDescriptor.Kind candidate : ServiceDescriptor.Kind.values() )
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
        final boolean optional =
          AnnotationsUtil.hasAnnotationOfType( parameter, GeneratorUtil.NULLABLE_ANNOTATION_CLASSNAME );
        if ( optional && ServiceDescriptor.Kind.INSTANCE != kind )
        {
          throw new ProcessorException( MemberChecks.mustNot( Constants.INJECTABLE_CLASSNAME,
                                                              "contain a constructor with a parameter annotated with " +
                                                              MemberChecks.toSimpleName( GeneratorUtil.NULLABLE_ANNOTATION_CLASSNAME ) +
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
        return new ServiceDescriptor( kind, service, parameter, parameterIndex );
      }
    }
  }

  @Nonnull
  private String getQualifier( @Nonnull final Element element )
  {
    final AnnotationMirror annotation = AnnotationsUtil.findAnnotationByType( element, Constants.NAMED_CLASSNAME );
    return null == annotation ? "" : AnnotationsUtil.getAnnotationValueValue( annotation, "value" );
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

  private boolean isParameterized( @Nonnull final DeclaredType nestedParameterType )
  {
    return !( (TypeElement) nestedParameterType.asElement() ).getTypeParameters().isEmpty();
  }
}
