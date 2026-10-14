package com.example.injectable.dependency;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_NullableDependencyModel {
  private Sting_NullableDependencyModel() {}

  public static NullableDependencyModel create(final @Nullable Runnable runnable) {
    return new NullableDependencyModel(runnable);
  }
}
