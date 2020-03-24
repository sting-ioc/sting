package sting.processor;

import java.util.Collection;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class Edge
{
  /**
   * The node that declared the service request.
   */
  @Nonnull
  private final Node _node;
  /**
   * The service that has been requested by the node.
   */
  @Nonnull
  private final ServiceRequest _serviceRequest;
  /**
   * The node(s) used to satisfy the service.
   * May be null if the service is optional and no node exists to satisfy service.
   */
  @Nullable
  private Collection<Node> _satisfiedBy;

  Edge( @Nonnull final Node node, @Nonnull final ServiceRequest serviceRequest )
  {
    _node = Objects.requireNonNull( node );
    _serviceRequest = Objects.requireNonNull( serviceRequest );
  }

  void setSatisfiedBy( @Nonnull final Collection<Node> satisfiedBy )
  {
    assert !satisfiedBy.isEmpty() ||
           _serviceRequest.getService().isOptional() ||
           _serviceRequest.getKind().isCollection();
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
  ServiceRequest getServiceRequest()
  {
    return _serviceRequest;
  }

  @Nonnull
  Collection<Node> getSatisfiedBy()
  {
    assert null != _satisfiedBy;
    assert !_satisfiedBy.isEmpty() ||
           _serviceRequest.getService().isOptional() ||
           _serviceRequest.getKind().isCollection();
    return _satisfiedBy;
  }
}
