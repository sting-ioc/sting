package sting.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.stream.JsonGenerator;

final class ObjectGraph
{
  /**
   * The injector that defines the graph.
   */
  @Nonnull
  private final InjectorDescriptor _injector;
  /**
   * The set of injectables explicitly included in the graph.
   */
  @Nonnull
  private final Map<String, InjectableDescriptor> _injectables = new HashMap<>();
  /**
   * The set of fragments explicitly included in the graph.
   */
  @Nonnull
  private final Map<String, FragmentDescriptor> _fragments = new HashMap<>();
  /**
   * The set of bindings included in the graph derived from descriptors.
   */
  @Nonnull
  private final List<Binding> _bindings = new ArrayList<>();
  /**
   * The types that are published in the object graph.
   */
  @Nonnull
  private final Map<Coordinate, List<Binding>> _publishedTypes = new LinkedHashMap<>();
  /**
   * The nodes contained in the object graph.
   */
  @Nonnull
  private final Map<Binding, Node> _nodes = new HashMap<>();
  /**
   * The node that represents the Injector.
   */
  @Nonnull
  private final Node _rootNode;
  /**
   * true when the graph has been completely built.
   */
  private boolean _complete;
  /**
   * The list of nodes included in graph in stable order based on depth in graph and node id.
   */
  @Nullable
  private List<Node> _orderedNodes;
  /**
   * The list of fragment nodes included in graph in stable order based on name.
   */
  @Nullable
  private List<FragmentNode> _fragmentNodes;

  ObjectGraph( @Nonnull final InjectorDescriptor injector )
  {
    _injector = Objects.requireNonNull( injector );
    _rootNode = new Node( this );
  }

  @Nonnull
  InjectorDescriptor getInjector()
  {
    return _injector;
  }

  @Nonnull
  Node getRootNode()
  {
    return _rootNode;
  }

  @Nonnull
  Node findOrCreateNode( @Nonnull final Binding binding )
  {
    assert !_complete;
    return _nodes.computeIfAbsent( binding, b -> new Node( this, b ) );
  }

  @Nonnull
  List<Node> getNodes()
  {
    assert null != _orderedNodes;
    return _orderedNodes;
  }

  @Nonnull
  List<FragmentNode> getFragments()
  {
    assert null != _fragmentNodes;
    return _fragmentNodes;
  }

  void complete()
  {
    assert !_complete;
    _complete = true;
    final AtomicInteger index = new AtomicInteger();
    final Map<FragmentDescriptor, FragmentNode> fragmentMap = new HashMap<>();
    _fragmentNodes = _nodes
      .values()
      .stream()
      .filter( Node::isFromProvides )
      .map( n -> (FragmentDescriptor) n.getBinding().getOwner() )
      .sorted( Comparator.comparing( FragmentDescriptor::getQualifiedTypeName ) )
      .map( f -> new FragmentNode( f, "fragment" + index.incrementAndGet() ) )
      .peek( f -> fragmentMap.put( f.getFragment(), f ) )
      .collect( Collectors.toList() );
    index.set( 0 );
    _orderedNodes = _nodes.values()
      .stream()
      .sorted( Comparator.comparing( Node::getDepth ).thenComparing( n -> n.getBinding().getId() ).reversed() )
      .peek( n -> n.setName( "node" + index.incrementAndGet() ) )
      .peek( n -> {
        if ( n.isFromProvides() )
        {
          //noinspection SuspiciousMethodCalls
          n.setFragment( fragmentMap.get( n.getBinding().getOwner() ) );
        }
      } )
      .collect( Collectors.toList() );
  }

  /**
   * Register the binding in the object graph.
   *
   * @param binding the binding.
   */
  private void registerBinding( @Nonnull final Binding binding )
  {
    _bindings.add( binding );
    binding.getCoordinates()
      .forEach( coordinate -> _publishedTypes.computeIfAbsent( coordinate, c -> new ArrayList<>() ).add( binding ) );
  }

  @Nonnull
  List<Binding> findAllBindingsByCoordinate( @Nonnull final Coordinate coordinate )
  {
    return _publishedTypes.getOrDefault( coordinate, Collections.emptyList() );
  }

  /**
   * Include the injectable in the object graph.
   *
   * @param injectable the injectable.
   */
  void registerInjectable( @Nonnull final InjectableDescriptor injectable )
  {
    _injectables.put( injectable.getElement().getQualifiedName().toString(), injectable );
    registerBinding( injectable.getBinding() );
  }

  /**
   * Include the fragment in the object graph.
   * It is assumed that the types included by the fragment have already been included in the ObjectGraph.
   *
   * @param fragment the fragment.
   */
  void registerFragment( @Nonnull final FragmentDescriptor fragment )
  {
    _fragments.put( fragment.getElement().getQualifiedName().toString(), fragment );
    for ( final Binding binding : fragment.getBindings() )
    {
      registerBinding( binding );
    }
  }

  void write( final JsonGenerator g )
  {
    g.writeStartObject();
    g.write( "schema", "graph/1" );

    g.writeStartArray( "values" );

    for ( final Node node : getNodes() )
    {
      node.write( g );
    }
    g.writeEnd();

    g.writeEnd();
  }
}
