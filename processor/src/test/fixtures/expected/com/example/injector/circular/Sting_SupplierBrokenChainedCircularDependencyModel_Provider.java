package com.example.injector.circular;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_SupplierBrokenChainedCircularDependencyModel_Provider {
  @Nonnull
  default SupplierBrokenChainedCircularDependencyModel provide() {
    return new Sting_SupplierBrokenChainedCircularDependencyModel();
  }

  default SupplierBrokenChainedCircularDependencyModel.MyModel1 getMyModel1(
      final SupplierBrokenChainedCircularDependencyModel injector) {
    return injector.getMyModel1();
  }

  default SupplierBrokenChainedCircularDependencyModel.MyModel2 getMyModel2(
      final SupplierBrokenChainedCircularDependencyModel injector) {
    return injector.getMyModel2();
  }

  default SupplierBrokenChainedCircularDependencyModel.MyModel3 getMyModel3(
      final SupplierBrokenChainedCircularDependencyModel injector) {
    return injector.getMyModel3();
  }
}
