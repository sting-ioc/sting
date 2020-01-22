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
}
