package sting.processor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.proton.ProcessorException;

final class CircularDependencyChecker
{
  private CircularDependencyChecker()
  {
  }

  static void verifyNoCircularDependencyLoops( @Nonnull final ObjectGraph graph )
  {
    final Set<Node> completed = new HashSet<>();

    verifyNoCircularDependenciesForRootNode( graph, graph.getRootNode(), completed );

    for ( final Node node : graph.getNodes() )
    {
      if ( node.getUsedBy().isEmpty() )
      {
        verifyNoCircularDependenciesForRootNode( graph, node, completed );
      }
    }
  }

  private static void verifyNoCircularDependenciesForRootNode( @Nonnull final ObjectGraph graph,
                                                               @Nonnull final Node node,
                                                               @Nonnull final Set<Node> completed )
  {
    final Stack<Entry> stack = new Stack<>();
    final Entry entry = new Entry( node, null );
    verifyNoCircularDependenciesForNode( graph, entry, stack, completed );
    assert stack.isEmpty();
  }

  private static void verifyNoCircularDependenciesForNode( @Nonnull final ObjectGraph graph,
                                                           @Nonnull final Entry entry,
                                                           @Nonnull final Stack<Entry> stack,
                                                           @Nonnull final Set<Node> completed )
  {
    stack.add( entry );
    for ( final Edge edge : entry._node.getDependsOn() )
    {
      for ( final Node node : edge.getSatisfiedBy() )
      {
        final Entry childEntry = new Entry( node, edge );
        final int indexOfMatching =
          doesEdgeBreakDependencyChain( edge ) ? -1 : detectCircularDependency( stack, node );
        if ( -1 != indexOfMatching )
        {
          throw new ProcessorException( "Injector contains a circular dependency.\n" +
                                        "Path:\n" +
                                        describeCircularDependencyPath( stack, childEntry ),
                                        graph.getInjector().getElement() );
        }
        else
        {
          if ( !completed.contains( node ) )
          {
            completed.add( entry._node );

            final int size = stack.size();
            verifyNoCircularDependenciesForNode( graph, childEntry, stack, completed );
            assert size == stack.size();
          }
        }
      }
    }
    stack.pop();
  }

  private static int detectCircularDependency( @Nonnull final Stack<Entry> stack,
                                               @Nonnull final Node node )
  {
    int index = stack.size() - 1;
    while ( index > 0 )
    {
      final Entry entry = stack.get( index );
      if ( doesEdgeBreakDependencyChain( entry._edge ) )
      {
        return -1;
      }
      else if ( entry._node == node )
      {
        return index - 1;
      }
      else
      {
        index--;
      }
    }
    return -1;
  }

  /**
   * Return true if circular dependency check does not need to check backwards over edge.
   *
   * @param edge the edge.
   * @return true if edge means dependency chekcing can cease.
   */
  private static boolean doesEdgeBreakDependencyChain( @Nullable final Edge edge )
  {
    return null == edge || edge.getDependency().getType().isSupplier();
  }

  /**
   * Generate a description of the dependency stack that includes a circular dependency.
   *
   * @param callStack the dependency stack.
   * @param badEntry  the entry that depends upon itself.
   * @return a string description.
   */
  @Nonnull
  private static String describeCircularDependencyPath( @Nonnull final Stack<Entry> callStack,
                                                        @Nonnull final Entry badEntry )
  {
    final StringBuilder sb = new StringBuilder();

    boolean matched = false;
    for ( final Entry entry : callStack )
    {
      final Node node = entry._node;
      sb.append( "  " );
      sb.append( getNodeTypeLabel( node ) );
      if ( node == badEntry._node )
      {
        sb.append( "+-< " );
        matched = true;
      }
      else if ( matched )
      {
        sb.append( "|   " );
      }
      else
      {
        sb.append( "    " );
      }
      sb.append( node.describeBinding() );

      sb.append( "\n" );
    }

    final Node node = badEntry._node;
    sb.append( "  " );
    sb.append( getNodeTypeLabel( node ) );
    sb.append( "+-> " );
    sb.append( node.describeBinding() );
    sb.append( "\n" );

    return sb.toString();
  }

  @Nonnull
  private static String getNodeTypeLabel( @Nonnull final Node node )
  {
    if ( node.hasNoBinding() )
    {
      return "[Injector]   ";
    }
    else
    {
      final Binding binding = node.getBinding();
      if ( Binding.Type.INJECTABLE == binding.getBindingType() )
      {
        return "[Injectable] ";
      }
      else
      {
        assert Binding.Type.PROVIDES == binding.getBindingType() ||
               Binding.Type.NULLABLE_PROVIDES == binding.getBindingType();
        return "[Provides]   ";
      }
    }
  }

  private static class Entry
  {
    @Nonnull
    private final Node _node;
    @Nullable
    private final Edge _edge;

    Entry( @Nonnull final Node node, @Nullable final Edge edge )
    {
      _node = Objects.requireNonNull( node );
      _edge = edge;
    }
  }
}
