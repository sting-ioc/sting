package com.example.injector.circular;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Generated;

@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyFragment implements SupplierBrokenFragmentWalkingCircularDependencyModel.MyFragment {
  @SuppressWarnings("unchecked")
  public Object $sting$_provideMyModel2(final Object model) {
    return provideMyModel2( Objects.requireNonNull( (Supplier<SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel1>) model ) );
  }
}
