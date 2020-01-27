package sting.processor;

import java.util.HashSet;
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
    final Stack<PathEntry> stack = new Stack<>();
    final PathEntry entry = new PathEntry( node, null );
    verifyNoCircularDependenciesForNode( graph, entry, stack, completed );
    assert stack.isEmpty();
  }

  private static void verifyNoCircularDependenciesForNode( @Nonnull final ObjectGraph graph,
                                                           @Nonnull final PathEntry entry,
                                                           @Nonnull final Stack<PathEntry> stack,
                                                           @Nonnull final Set<Node> completed )
  {
    stack.add( entry );
    for ( final Edge edge : entry.getNode().getDependsOn() )
    {
      for ( final Node node : edge.getSatisfiedBy() )
      {
        final PathEntry childEntry = new PathEntry( node, edge );
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
            completed.add( entry.getNode() );

            final int size = stack.size();
            verifyNoCircularDependenciesForNode( graph, childEntry, stack, completed );
            assert size == stack.size();
          }
        }
      }
    }
    stack.pop();
  }

  private static int detectCircularDependency( @Nonnull final Stack<PathEntry> stack,
                                               @Nonnull final Node node )
  {
    int index = stack.size() - 1;
    while ( index > 0 )
    {
      final PathEntry entry = stack.get( index );
      if ( doesEdgeBreakDependencyChain( entry.getEdge() ) )
      {
        return -1;
      }
      else if ( entry.getNode() == node )
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
    return null == edge || edge.getDependency().getKind().isSupplier();
  }

  /**
   * Generate a description of the dependency stack that includes a circular dependency.
   *
   * @param stack    the dependency stack.
   * @param badEntry the entry that depends upon itself.
   * @return a string description.
   */
  @Nonnull
  private static String describeCircularDependencyPath( @Nonnull final Stack<PathEntry> stack,
                                                        @Nonnull final PathEntry badEntry )
  {
    final StringBuilder sb = new StringBuilder();

    boolean matched = false;
    for ( final PathEntry entry : stack )
    {
      final Node node = entry.getNode();
      final String connector;
      if ( node == badEntry.getNode() )
      {
        connector = "+-<";
        matched = true;
      }
      else if ( matched )
      {
        connector = "|  ";
      }
      else
      {
        connector = "   ";
      }
      sb.append( node.describe( connector ) );

      sb.append( "\n" );
    }

    sb.append( badEntry.getNode().describe( "+->" ) );
    sb.append( "\n" );

    return sb.toString();
  }
}
