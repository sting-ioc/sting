package com.example.fragment.dependency;

import java.util.EventListener;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_ComplexDependencyModel implements ComplexDependencyModel {
  public Runnable $sting$_provideRunnable(final String name, final int priority,
      final @Nullable EventListener listener) {
    return provideRunnable( name, priority, listener );
  }
}
