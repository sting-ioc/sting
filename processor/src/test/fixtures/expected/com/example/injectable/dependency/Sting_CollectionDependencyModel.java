package com.example.injectable.dependency;

import java.util.Collection;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_CollectionDependencyModel {
  private Sting_CollectionDependencyModel() {
  }

  @Nonnull
  public static CollectionDependencyModel create(final Collection<Runnable> runnable) {
    return new CollectionDependencyModel( Objects.requireNonNull( runnable ) );
  }
}
