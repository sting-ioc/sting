package com.example.injectable.dependency;

import java.util.EventListener;
import java.util.Objects;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_MultipleDependencyModel {
  private Sting_MultipleDependencyModel() {
  }

  @SuppressWarnings({
      "ImpossibleNullComparison",
      "UnnecessaryCheckNotNull"
  })
  public static MultipleDependencyModel create(final Runnable runnable,
      final EventListener listener) {
    return new MultipleDependencyModel( Objects.requireNonNull( runnable ), Objects.requireNonNull( listener ) );
  }
}
