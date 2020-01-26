package com.example.fragment.dependency;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_SupplierDependencyModel implements SupplierDependencyModel {
  public Runnable $sting$_provideRunnable(final Supplier<String> name) {
    return provideRunnable( Objects.requireNonNull( name ) );
  }
}
