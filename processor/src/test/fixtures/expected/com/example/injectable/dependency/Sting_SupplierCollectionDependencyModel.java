package com.example.injectable.dependency;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_SupplierCollectionDependencyModel {
  private Sting_SupplierCollectionDependencyModel() {
  }

  @SuppressWarnings({
      "ImpossibleNullComparison",
      "UnnecessaryCheckNotNull"
  })
  public static SupplierCollectionDependencyModel create(
      final Collection<Supplier<Runnable>> runnable) {
    return new SupplierCollectionDependencyModel( Objects.requireNonNull( runnable ) );
  }
}
