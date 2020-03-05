package sting.processor;

import java.util.Collection;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class Edge
{
  /**
   * The node that declared the service dependency.
   */
  @Nonnull
  private final Node _node;
  /**
   * The service that needs to be satisfied.
   */
  @Nonnull
  private final ServiceDescriptor _service;
  /**
   * The node(s) used to satisfy the service .
   * May be null if the service is optional and no node exists to satisfy service.
   */
  @Nullable
  private Collection<Node> _satisfiedBy;

  Edge( @Nonnull final Node node, @Nonnull final ServiceDescriptor service )
  {
    _node = Objects.requireNonNull( node );
    _service = Objects.requireNonNull( service );
  }

  void setSatisfiedBy( @Nonnull final Collection<Node> satisfiedBy )
  {
    assert !satisfiedBy.isEmpty() || _service.getService().isOptional() || _service.getKind().isCollection();
    _satisfiedBy = satisfiedBy;
    for ( final Node node : satisfiedBy )
    {
      node.usedBy( this );
      node.setDepth( Math.min( _node.getDepth() + 1, node.getDepth() ) );
    }
  }

  @Nonnull
  Node getNode()
  {
    return _node;
  }

  @Nonnull
  ServiceDescriptor getService()
  {
    return _service;
  }

  @Nonnull
  Collection<Node> getSatisfiedBy()
  {
    assert null != _satisfiedBy;
    assert !_satisfiedBy.isEmpty() || _service.getService().isOptional() || _service.getKind().isCollection();
    return _satisfiedBy;
  }
}
