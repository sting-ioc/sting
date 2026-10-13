package com.example.injectable.dependency;

import java.util.Objects;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_NonnullDependencyModel {
  private Sting_NonnullDependencyModel() {}

  @SuppressWarnings({"ImpossibleNullComparison", "UnnecessaryCheckNotNull"})
  public static NonnullDependencyModel create(final Runnable runnable) {
    return new NonnullDependencyModel(Objects.requireNonNull(runnable));
  }
}
