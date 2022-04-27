package com.example.injectable.dependency;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_NonnullDependencyModel {
  private Sting_NonnullDependencyModel() {
  }

  @Nonnull
  public static NonnullDependencyModel create(@Nonnull final Runnable runnable) {
    return new NonnullDependencyModel( Objects.requireNonNull( runnable ) );
  }
}
