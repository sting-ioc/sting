package com.example.fragment.dependency;

import java.util.EventListener;
import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
public final class Sting_ComplexDependencyModel implements ComplexDependencyModel {
  public Runnable $sting$_provideRunnable(final String name, final int priority,
      @Nullable final EventListener listener) {
    return provideRunnable( Objects.requireNonNull( name ), Objects.requireNonNull( priority ), listener );
  }
}
