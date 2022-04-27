package com.example.injectable.dependency;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_NullableDependencyModel {
  private Sting_NullableDependencyModel() {
  }

  @Nonnull
  public static NullableDependencyModel create(@Nullable final Runnable runnable) {
    return new NullableDependencyModel( runnable );
  }
}
