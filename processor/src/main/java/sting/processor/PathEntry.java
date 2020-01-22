package sting.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class PathEntry
{
  @Nonnull
  private final Node _node;
  @Nullable
  private final Edge _edge;

  PathEntry( @Nonnull final Node node, @Nullable final Edge edge )
  {
    assert null != edge || node.hasNoBinding() || node.isEager();
    _node = Objects.requireNonNull( node );
    _edge = edge;
  }

  @Nonnull
  Node getNode()
  {
    return _node;
  }

  @Nullable
  Edge getEdge()
  {
    return _edge;
  }
}
