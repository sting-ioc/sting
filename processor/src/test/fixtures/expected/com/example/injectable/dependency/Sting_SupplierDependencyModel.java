package com.example.injectable.dependency;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_SupplierDependencyModel {
  private Sting_SupplierDependencyModel() {
  }

  @Nonnull
  public static SupplierDependencyModel create(final Supplier<Runnable> runnable) {
    return new SupplierDependencyModel( Objects.requireNonNull( runnable ) );
  }
}
