package com.example.fragment.dependency;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_SupplierOptionalDependencyModel implements SupplierOptionalDependencyModel {
  public Runnable $sting$_provideRunnable(final Supplier<Optional<String>> name) {
    return provideRunnable( Objects.requireNonNull( name ) );
  }
}
