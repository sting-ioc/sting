package com.example.injectable.dependency;

import java.util.EventListener;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_MultipleDependencyModel {
  private Sting_MultipleDependencyModel() {
  }

  @Nonnull
  public static MultipleDependencyModel create(final Runnable runnable,
      final EventListener listener) {
    return new MultipleDependencyModel( Objects.requireNonNull( runnable ), Objects.requireNonNull( listener ) );
  }
}
