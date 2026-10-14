package com.example.injectable.dependency;

import java.util.Optional;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_OptionalDependencyModel {
  private Sting_OptionalDependencyModel() {}

  public static OptionalDependencyModel create(final Optional<Runnable> runnable) {
    return new OptionalDependencyModel(runnable);
  }
}
