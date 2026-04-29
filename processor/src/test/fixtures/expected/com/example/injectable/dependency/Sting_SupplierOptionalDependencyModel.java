package com.example.injectable.dependency;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_SupplierOptionalDependencyModel {
  private Sting_SupplierOptionalDependencyModel() {
  }

  @Nonnull
  public static SupplierOptionalDependencyModel create(final Supplier<Optional<Runnable>> runnable) {
    return new SupplierOptionalDependencyModel( Objects.requireNonNull( runnable ) );
  }
}
