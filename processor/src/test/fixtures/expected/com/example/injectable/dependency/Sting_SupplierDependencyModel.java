package com.example.injectable.dependency;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_SupplierDependencyModel {
  private Sting_SupplierDependencyModel() {
  }

  @SuppressWarnings({
      "ImpossibleNullComparison",
      "UnnecessaryCheckNotNull"
  })
  public static SupplierDependencyModel create(final Supplier<Runnable> runnable) {
    return new SupplierDependencyModel( Objects.requireNonNull( runnable ) );
  }
}
