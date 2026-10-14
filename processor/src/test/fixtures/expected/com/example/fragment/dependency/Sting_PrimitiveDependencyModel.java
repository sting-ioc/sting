package com.example.fragment.dependency;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_PrimitiveDependencyModel implements PrimitiveDependencyModel {
  public Runnable $sting$_provideRunnable(final boolean bool, final char ch, final byte b,
      final short s, final int priority, final long l, final float f, final double d) {
    return provideRunnable( bool, ch, b, s, priority, l, f, d );
  }
}
