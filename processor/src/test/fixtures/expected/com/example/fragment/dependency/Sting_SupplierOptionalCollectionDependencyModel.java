package com.example.fragment.dependency;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_SupplierOptionalCollectionDependencyModel implements SupplierOptionalCollectionDependencyModel {
  public Runnable $sting$_provideRunnable(final Collection<Supplier<Optional<String>>> name) {
    return provideRunnable( Objects.requireNonNull( name ) );
  }
}
