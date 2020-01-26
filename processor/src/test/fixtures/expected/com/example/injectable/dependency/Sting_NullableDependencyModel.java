package com.example.injectable.dependency;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
public final class Sting_NullableDependencyModel {
  private Sting_NullableDependencyModel() {
  }

  @Nonnull
  public static NullableDependencyModel create(@Nullable final Runnable runnable) {
    return new NullableDependencyModel( runnable );
  }
}
