package com.example.injectable.dependency;

import java.util.Objects;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_BasicDependencyModel {
  private Sting_BasicDependencyModel() {}

  public static BasicDependencyModel create(final Runnable runnable) {
    return new BasicDependencyModel(Objects.requireNonNull(runnable));
  }
}
