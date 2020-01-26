package com.example.injectable.dependency;

import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_PrimitiveDependencyModel {
  private Sting_PrimitiveDependencyModel() {
  }

  @Nonnull
  public static PrimitiveDependencyModel create(final boolean bool, final char ch, final byte b,
      final short s, final int countDown, final long l, final float f, final double d) {
    return new PrimitiveDependencyModel( bool, ch, b, s, countDown, l, f, d );
  }
}
