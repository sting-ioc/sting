package com.example.injectable.dependency;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_SupplierOptionalCollectionDependencyModel {
  private Sting_SupplierOptionalCollectionDependencyModel() {
  }

  @Nonnull
  public static SupplierOptionalCollectionDependencyModel create(
      final Collection<Supplier<Optional<Runnable>>> runnable) {
    return new SupplierOptionalCollectionDependencyModel( Objects.requireNonNull( runnable ) );
  }
}
