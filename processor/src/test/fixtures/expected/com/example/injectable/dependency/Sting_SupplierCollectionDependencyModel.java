package com.example.injectable.dependency;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_SupplierCollectionDependencyModel {
  private Sting_SupplierCollectionDependencyModel() {
  }

  @Nonnull
  public static SupplierCollectionDependencyModel create(
      final Collection<Supplier<Runnable>> runnable) {
    return new SupplierCollectionDependencyModel( Objects.requireNonNull( runnable ) );
  }
}
