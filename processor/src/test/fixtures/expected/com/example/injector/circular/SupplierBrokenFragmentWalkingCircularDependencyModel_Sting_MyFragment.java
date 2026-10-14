package com.example.injector.circular;

import java.util.function.Supplier;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyFragment implements SupplierBrokenFragmentWalkingCircularDependencyModel.MyFragment {
  @SuppressWarnings({
      "rawtypes",
      "unchecked"
  })
  public Runnable $sting$_provideRunnable(final Supplier model) {
    return provideRunnable( (Supplier<SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel1>) model );
  }
}
