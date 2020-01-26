package com.example.injector.circular;

import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyModel1 {
  private SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyModel1() {
  }

  @Nonnull
  public static SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel1 create(
      final SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel2 model) {
    return new SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel1( model );
  }
}
