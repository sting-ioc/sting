package com.example.injectable.dependency;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_SupplierOptionalDependencyModel {
  private Sting_SupplierOptionalDependencyModel() {
  }

  @SuppressWarnings({
      "ImpossibleNullComparison",
      "UnnecessaryCheckNotNull"
  })
  public static SupplierOptionalDependencyModel create(
      final Supplier<Optional<Runnable>> runnable) {
    return new SupplierOptionalDependencyModel( Objects.requireNonNull( runnable ) );
  }
}
