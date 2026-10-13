package com.example.fragment.dependency;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_NullableDependencyModel implements NullableDependencyModel {
  @SuppressWarnings({
      "ImpossibleNullComparison",
      "UnnecessaryCheckNotNull"
  })
  public Runnable $sting$_provideRunnable(final @Nullable String name) {
    return provideRunnable( name );
  }
}
