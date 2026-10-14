package com.example.injectable.dependency;

import java.io.Serializable;
import java.util.EventListener;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_ComplexDependencyModel {
  private Sting_ComplexDependencyModel() {}

  public static ComplexDependencyModel create(
      final Runnable runnable,
      final @Nullable EventListener listener,
      final Serializable serializable,
      final int countDown) {
    return new ComplexDependencyModel(runnable, listener, serializable, countDown);
  }
}
