package com.example.fragment.dependency;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_NonnullDependencyModel implements NonnullDependencyModel {
  public Runnable $sting$_provideRunnable(@Nonnull final String name) {
    return provideRunnable( Objects.requireNonNull( name ) );
  }
}
