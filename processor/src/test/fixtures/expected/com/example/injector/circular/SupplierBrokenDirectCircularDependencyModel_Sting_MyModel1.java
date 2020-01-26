package com.example.injector.circular;

import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenDirectCircularDependencyModel_Sting_MyModel1 {
  private SupplierBrokenDirectCircularDependencyModel_Sting_MyModel1() {
  }

  @Nonnull
  public static SupplierBrokenDirectCircularDependencyModel.MyModel1 create(
      final SupplierBrokenDirectCircularDependencyModel.MyModel2 model) {
    return new SupplierBrokenDirectCircularDependencyModel.MyModel1( model );
  }
}
