package com.example.injector.circular;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenChainedCircularDependencyModel_Sting_MyModel3 {
  private SupplierBrokenChainedCircularDependencyModel_Sting_MyModel3() {
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public static SupplierBrokenChainedCircularDependencyModel.MyModel3 create(final Object model) {
    return new SupplierBrokenChainedCircularDependencyModel.MyModel3( Objects.requireNonNull( (Supplier<SupplierBrokenChainedCircularDependencyModel.MyModel1>) model ) );
  }
}
