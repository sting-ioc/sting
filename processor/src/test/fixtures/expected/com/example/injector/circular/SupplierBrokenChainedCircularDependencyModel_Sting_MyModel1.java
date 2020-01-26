package com.example.injector.circular;

import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenChainedCircularDependencyModel_Sting_MyModel1 {
  private SupplierBrokenChainedCircularDependencyModel_Sting_MyModel1() {
  }

  @Nonnull
  public static SupplierBrokenChainedCircularDependencyModel.MyModel1 create(
      final SupplierBrokenChainedCircularDependencyModel.MyModel2 model) {
    return new SupplierBrokenChainedCircularDependencyModel.MyModel1( model );
  }
}
