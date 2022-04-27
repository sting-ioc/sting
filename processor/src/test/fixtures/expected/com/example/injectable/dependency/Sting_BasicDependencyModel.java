package com.example.injectable.dependency;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_BasicDependencyModel {
  private Sting_BasicDependencyModel() {
  }

  @Nonnull
  public static BasicDependencyModel create(final Runnable runnable) {
    return new BasicDependencyModel( Objects.requireNonNull( runnable ) );
  }
}
