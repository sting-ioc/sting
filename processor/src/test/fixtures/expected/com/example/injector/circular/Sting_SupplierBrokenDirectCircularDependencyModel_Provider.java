package com.example.injector.circular;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_SupplierBrokenDirectCircularDependencyModel_Provider {
  @Nonnull
  default SupplierBrokenDirectCircularDependencyModel provide() {
    return new Sting_SupplierBrokenDirectCircularDependencyModel();
  }

  default SupplierBrokenDirectCircularDependencyModel.MyModel1 getMyModel1(
      final SupplierBrokenDirectCircularDependencyModel injector) {
    return injector.getMyModel1();
  }

  default SupplierBrokenDirectCircularDependencyModel.MyModel2 getMyModel2(
      final SupplierBrokenDirectCircularDependencyModel injector) {
    return injector.getMyModel2();
  }
}
