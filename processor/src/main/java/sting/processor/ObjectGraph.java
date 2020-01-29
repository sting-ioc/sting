package sting.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
   * The list of types included in the graph.
   * This is used to skip registers for types that are already present.
   * This can occur when we have diamond dependency chains.
   */
  @Nonnull
  private final Set<String> _includedTypes = new HashSet<>();
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
   * The index of ids to Node.
   */
  @Nonnull
  private final Map<String, Node> _nodesById = new HashMap<>();
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

  @Nullable
  Node findNodeById( @Nonnull final String id )
  {
    return _nodesById.get( id );
  }

  @Nonnull
  Node findOrCreateNode( @Nonnull final Binding binding )
  {
    assert !_complete;
    return _nodes.computeIfAbsent( binding, this::createNode );
  }

  @Nonnull
  private Node createNode( @Nonnull final Binding binding )
  {
    final String id = binding.getId();
    assert !_nodesById.containsKey( id );
    final Node node = new Node( this, binding );
    _nodesById.put( id, node );
    return node;
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
    final String typeName = injectable.getElement().getQualifiedName().toString();
    if ( _includedTypes.add( typeName ) )
    {
      final Binding binding = injectable.getBinding();
      registerBinding( binding );
      if ( binding.isEager() )
      {
        findOrCreateNode( binding );
      }
    }
  }

  /**
   * Include the fragment in the object graph.
   * It is assumed that the types included by the fragment have already been included in the ObjectGraph.
   *
   * @param fragment the fragment.
   */
  void registerFragment( @Nonnull final FragmentDescriptor fragment )
  {
    final String typeName = fragment.getElement().getQualifiedName().toString();
    if ( _includedTypes.add( typeName ) )
    {
      for ( final Binding binding : fragment.getBindings() )
      {
        registerBinding( binding );
      }
    }
  }

  void write( final JsonGenerator g )
  {
    g.writeStartObject();
    g.write( "schema", "graph/1" );

    g.writeStartArray( "nodes" );

    for ( final Node node : getNodes() )
    {
      node.write( g );
    }
    g.writeEnd();

    g.writeEnd();
  }
}
