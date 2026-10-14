package com.example.injectable.dependency;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_PrimitiveDependencyModel {
  private Sting_PrimitiveDependencyModel() {
  }

  public static PrimitiveDependencyModel create(final boolean bool, final char ch, final byte b,
      final short s, final int countDown, final long l, final float f, final double d) {
    return new PrimitiveDependencyModel( bool, ch, b, s, countDown, l, f, d );
  }
}
