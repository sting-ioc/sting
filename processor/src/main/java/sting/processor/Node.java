package sting.processor;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.stream.JsonGenerator;

final class Node
{
  /**
   * The object graph that created this node.
   */
  @Nonnull
  private final ObjectGraph _objectGraph;
  /**
   * The binding for the node.
   * May be null if it represents an Injector.
   */
  @Nullable
  private final Binding _binding;
  /**
   * The edges to nodes that this node depends upon.
   */
  @Nonnull
  private final Map<DependencyDescriptor, Edge> _dependsOn = new LinkedHashMap<>();
  /**
   * The edges to nodes that use this node.
   */
  @Nonnull
  private final Set<Edge> _usedBy = new HashSet<>();
  /**
   * True if Node is explicitly from an eager binding or implicitly eager by being
   * a (transitive) dependency of an eager binding.
   */
  private boolean _eager;
  /**
   * The shortest path from a top level dependency to this node.
   */
  private int _depth;

  /**
   * Constructor used to construct a Node for the Injector.
   *
   * @param objectGraph the object graph
   */
  Node( @Nonnull final ObjectGraph objectGraph )
  {
    this( objectGraph,
          null,
          objectGraph.getInjector().getTopLevelDependencies().toArray( new DependencyDescriptor[ 0 ] ) );
  }

  /**
   * Constructor used to construct a Node for a binding.
   *
   * @param binding the binding.
   */
  Node( final ObjectGraph objectGraph, @Nonnull final Binding binding )
  {
    this( objectGraph, binding, binding.getDependencies() );
  }

  private Node( @Nonnull final ObjectGraph objectGraph,
                @Nullable final Binding binding,
                @Nonnull final DependencyDescriptor[] dependencies )
  {
    _objectGraph = Objects.requireNonNull( objectGraph );
    _binding = binding;
    for ( final DependencyDescriptor dependency : dependencies )
    {
      _dependsOn.put( dependency, new Edge( this, dependency ) );
    }
  }

  boolean isEager()
  {
    return _eager;
  }

  void markNodeAndUpstreamAsEager()
  {
    if ( !_eager )
    {
      _eager = true;
      // Propagate eager flag to all nodes that this node uses unless
      // the dependency is a Supplier style dependency. Those can be non-eager
      // as they do not need to be created until they are accessed
      for ( final Edge edge : _dependsOn.values() )
      {
        if ( !edge.getDependency().getType().isSupplier() )
        {
          edge.getSatisfiedBy().forEach( Node::markNodeAndUpstreamAsEager );
        }
      }
    }
  }

  @Nonnull
  Binding getBinding()
  {
    assert null != _binding;
    return _binding;
  }

  @Nonnull
  Collection<Edge> getDependsOn()
  {
    return _dependsOn.values();
  }

  @Nonnull
  Set<Edge> getUsedBy()
  {
    return _usedBy;
  }

  int getDepth()
  {
    return _depth;
  }

  void setDepth( final int depth )
  {
    _depth = depth;
  }

  void usedBy( @Nonnull final Edge edge )
  {
    assert !_usedBy.contains( edge );
    _usedBy.add( edge );
  }

  void write( @Nonnull final JsonGenerator g )
  {
    g.writeStartObject();
    assert null != _binding;
    g.write( "id", _binding.getId() );
    final Binding.Type type = _binding.getBindingType();
    g.write( "bindingType", type.name() );
    if ( _eager )
    {
      g.write( "eager", true );
    }
    if ( !_dependsOn.isEmpty() )
    {
      g.writeStartArray( "dependencies" );
      for ( final Edge edge : _dependsOn.values() )
      {
        g.writeStartObject();
        edge.getDependency().getCoordinate().write( g );
        g.writeStartArray( "supportedBy" );
        for ( final Node node : edge.getSatisfiedBy() )
        {
          g.write( node.getBinding().getId() );
        }
        g.writeEnd();
        g.writeEnd();
      }
      g.writeEnd();
    }
    g.writeEnd();
  }
}
