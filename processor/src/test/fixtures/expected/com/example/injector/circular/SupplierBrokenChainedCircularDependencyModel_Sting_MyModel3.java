package com.example.injector.circular;

import java.util.function.Supplier;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenChainedCircularDependencyModel_Sting_MyModel3 {
  private SupplierBrokenChainedCircularDependencyModel_Sting_MyModel3() {
  }

  @Nonnull
  public static SupplierBrokenChainedCircularDependencyModel.MyModel3 create(
      final Supplier<SupplierBrokenChainedCircularDependencyModel.MyModel1> model) {
    return new SupplierBrokenChainedCircularDependencyModel.MyModel3( model );
  }
}
