package com.example.injector.circular;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyFragment implements SupplierBrokenFragmentWalkingCircularDependencyModel.MyFragment {
  @SuppressWarnings({
      "rawtypes",
      "unchecked"
  })
  public Runnable $sting$_provideRunnable(final Supplier model) {
    return provideRunnable( Objects.requireNonNull( (Supplier<SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel1>) model ) );
  }
}
