package com.example.injector.circular;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_SupplierBrokenFragmentWalkingCircularDependencyModel_Provider {
  @Nonnull
  default SupplierBrokenFragmentWalkingCircularDependencyModel provide() {
    return new Sting_SupplierBrokenFragmentWalkingCircularDependencyModel();
  }

  default SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel1 getMyModel1(
      final SupplierBrokenFragmentWalkingCircularDependencyModel injector) {
    return injector.getMyModel1();
  }

  default SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel2 getMyModel2(
      final SupplierBrokenFragmentWalkingCircularDependencyModel injector) {
    return injector.getMyModel2();
  }

  default SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel3 getMyModel3(
      final SupplierBrokenFragmentWalkingCircularDependencyModel injector) {
    return injector.getMyModel3();
  }
}
