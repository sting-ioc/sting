package sting.processor;

import java.util.Objects;
import java.util.Stack;
import javax.annotation.Nonnull;

final class WorkEntry
{
  @Nonnull
  private final PathEntry _entry;
  @Nonnull
  private final Stack<PathEntry> _stack;

  WorkEntry( @Nonnull final PathEntry entry, @Nonnull final Stack<PathEntry> stack )
  {
    _entry = Objects.requireNonNull( entry );
    _stack = Objects.requireNonNull( stack );
  }

  @Nonnull
  PathEntry getEntry()
  {
    return _entry;
  }

  @Nonnull
  Stack<PathEntry> getStack()
  {
    return _stack;
  }

  @Nonnull
  String describePathFromRoot()
  {
    final StringBuilder sb = new StringBuilder();

    final int size = _stack.size();
    for ( int i = 0; i < size; i++ )
    {
      final PathEntry entry = _stack.get( i );
      final Node node = entry.getNode();
      final String connector;
      if ( node.hasNoBinding() )
      {
        connector = "   ";
      }
      else if ( size - 1 == i )
      {
        connector = " * ";
      }
      else
      {
        connector = "   ";
      }
      sb.append( node.describe( connector ) );

      sb.append( "\n" );
    }

    return sb.toString();
  }
}
