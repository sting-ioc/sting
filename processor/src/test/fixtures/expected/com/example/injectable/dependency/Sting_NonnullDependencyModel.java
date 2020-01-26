package com.example.injectable.dependency;

import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_NonnullDependencyModel {
  private Sting_NonnullDependencyModel() {
  }

  @Nonnull
  public static NonnullDependencyModel create(@Nonnull final Runnable runnable) {
    return new NonnullDependencyModel( runnable );
  }
}
