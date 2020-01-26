package com.example.injectable.dependency;

import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_BasicDependencyModel {
  private Sting_BasicDependencyModel() {
  }

  @Nonnull
  public static BasicDependencyModel create(final Runnable runnable) {
    return new BasicDependencyModel( runnable );
  }
}
