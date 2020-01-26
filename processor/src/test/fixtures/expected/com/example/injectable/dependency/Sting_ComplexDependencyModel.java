package com.example.injectable.dependency;

import java.io.Serializable;
import java.util.EventListener;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
public final class Sting_ComplexDependencyModel {
  private Sting_ComplexDependencyModel() {
  }

  @Nonnull
  public static ComplexDependencyModel create(final Runnable runnable,
      @Nullable final EventListener listener, final Serializable serializable,
      final int countDown) {
    return new ComplexDependencyModel( runnable, listener, serializable, countDown );
  }
}
