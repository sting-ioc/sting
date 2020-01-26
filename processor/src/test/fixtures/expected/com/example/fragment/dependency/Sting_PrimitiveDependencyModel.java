package com.example.fragment.dependency;

import java.util.Objects;
import javax.annotation.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_PrimitiveDependencyModel implements PrimitiveDependencyModel {
  public Runnable $sting$_provideRunnable(final boolean bool, final char ch, final byte b,
      final short s, final int priority, final long l, final float f, final double d) {
    return provideRunnable( Objects.requireNonNull( bool ), Objects.requireNonNull( ch ), Objects.requireNonNull( b ), Objects.requireNonNull( s ), Objects.requireNonNull( priority ), Objects.requireNonNull( l ), Objects.requireNonNull( f ), Objects.requireNonNull( d ) );
  }
}
