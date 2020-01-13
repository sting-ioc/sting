package sting.processor;

import java.util.Collection;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class Edge
{
  /**
   * The node that declared the dependency.
   */
  @Nullable
  private final Node _node;
  /**
   * The dependency that needs to be satisfied.
   */
  @Nonnull
  private final DependencyDescriptor _dependency;
  /**
   * The node(s) used to satisfy the dependency.
   * May be null if the dependency is optional and no node exists to satisfy dependency.
   */
  @Nullable
  private Collection<Node> _satisfiedBy;

  Edge( @Nullable final Node node, @Nonnull final DependencyDescriptor dependency )
  {
    _node = node;
    _dependency = Objects.requireNonNull( dependency );
  }

  void setSatisfiedBy( @Nonnull final Collection<Node> satisfiedBy )
  {
    assert !satisfiedBy.isEmpty() || _dependency.isOptional();
    _satisfiedBy = satisfiedBy;
    for ( final Node node : satisfiedBy )
    {
      node.usedBy( this );
    }
  }

  @Nullable
  Node getNode()
  {
    return _node;
  }

  @Nonnull
  DependencyDescriptor getDependency()
  {
    return _dependency;
  }

  boolean isValid()
  {
    return isSatisfied() || _dependency.isOptional();
  }

  boolean isSatisfied()
  {
    return null != _satisfiedBy;
  }

  @Nullable
  Collection<Node> getSatisfiedBy()
  {
    assert null != _satisfiedBy && ( !_satisfiedBy.isEmpty() ) || _dependency.isOptional();
    return _satisfiedBy;
  }
}
