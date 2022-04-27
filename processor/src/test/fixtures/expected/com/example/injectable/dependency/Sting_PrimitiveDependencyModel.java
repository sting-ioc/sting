package com.example.injectable.dependency;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_PrimitiveDependencyModel {
  private Sting_PrimitiveDependencyModel() {
  }

  @Nonnull
  public static PrimitiveDependencyModel create(final boolean bool, final char ch, final byte b,
      final short s, final int countDown, final long l, final float f, final double d) {
    return new PrimitiveDependencyModel( Objects.requireNonNull( bool ), Objects.requireNonNull( ch ), Objects.requireNonNull( b ), Objects.requireNonNull( s ), Objects.requireNonNull( countDown ), Objects.requireNonNull( l ), Objects.requireNonNull( f ), Objects.requireNonNull( d ) );
  }
}
