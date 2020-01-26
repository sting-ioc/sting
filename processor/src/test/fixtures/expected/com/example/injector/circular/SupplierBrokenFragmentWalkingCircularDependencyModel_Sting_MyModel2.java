package com.example.injector.circular;

import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyModel2 {
  private SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyModel2() {
  }

  @Nonnull
  public static SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel2 create(
      final SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel3 model) {
    return new SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel2( model );
  }
}
