package com.example.injectable.dependency;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_OptionalDependencyModel {
  private Sting_OptionalDependencyModel() {
  }

  @Nonnull
  public static OptionalDependencyModel create(final Optional<Runnable> runnable) {
    return new OptionalDependencyModel( Objects.requireNonNull( runnable ) );
  }
}
