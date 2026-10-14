package com.example.fragment.dependency;

import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_SupplierOptionalDependencyModel
    implements SupplierOptionalDependencyModel {
  public Runnable $sting$_provideRunnable(final Supplier<Optional<String>> name) {
    return provideRunnable(name);
  }
}
