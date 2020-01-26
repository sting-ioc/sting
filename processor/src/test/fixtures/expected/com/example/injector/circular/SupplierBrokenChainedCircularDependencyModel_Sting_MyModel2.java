package com.example.injector.circular;

import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenChainedCircularDependencyModel_Sting_MyModel2 {
  private SupplierBrokenChainedCircularDependencyModel_Sting_MyModel2() {
  }

  @Nonnull
  public static SupplierBrokenChainedCircularDependencyModel.MyModel2 create(
      final SupplierBrokenChainedCircularDependencyModel.MyModel3 model) {
    return new SupplierBrokenChainedCircularDependencyModel.MyModel2( model );
  }
}
