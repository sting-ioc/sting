package com.example.injectable.dependency;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_SupplierOptionalCollectionDependencyModel {
  private Sting_SupplierOptionalCollectionDependencyModel() {
  }

  public static SupplierOptionalCollectionDependencyModel create(
      final Collection<Supplier<Optional<Runnable>>> runnable) {
    return new SupplierOptionalCollectionDependencyModel( runnable );
  }
}
