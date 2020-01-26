package com.example.injectable.dependency;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_QualifiedDependencyModel {
  private Sting_QualifiedDependencyModel() {
  }

  @Nonnull
  public static QualifiedDependencyModel create(final Runnable runnable) {
    return new QualifiedDependencyModel( Objects.requireNonNull( runnable ) );
  }
}
