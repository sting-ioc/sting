package com.example.injectable.dependency;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_QualifiedDependencyModel {
  private Sting_QualifiedDependencyModel() {
  }

  @Nonnull
  public static QualifiedDependencyModel create(final Runnable runnable) {
    return new QualifiedDependencyModel( Objects.requireNonNull( runnable ) );
  }
}
