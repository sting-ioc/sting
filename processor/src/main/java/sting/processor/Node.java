package sting.processor;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class Node
{
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
   * Constructor used to construct a Node for the Injector.
   *
   * @param dependencies the dependencies.
   */
  Node( @Nonnull final DependencyDescriptor[] dependencies )
  {
    this( null, dependencies );
  }

  /**
   * Constructor used to construct a Node for a binding.
   *
   * @param binding the binding.
   */
  Node( @Nonnull final Binding binding )
  {
    this( binding, binding.getDependencies() );
  }

  private Node( @Nullable final Binding binding, @Nonnull final DependencyDescriptor[] dependencies )
  {
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
      // Propagate eager flag to all nodes that uses this node unless
      // the dependency is a Supplier style dependency. Those can be non-eager
      // as they do not need to be created until they are accessed
      for ( final Edge edge : _usedBy )
      {
        final Node upstream = edge.getNode();
        if ( !edge.getDependency().getType().isSupplier() )
        {
          upstream.markNodeAndUpstreamAsEager();
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

  void usedBy( @Nonnull final Edge edge )
  {
    assert !_usedBy.contains( edge );
    _usedBy.add( edge );
  }
}
