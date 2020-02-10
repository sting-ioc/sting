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
}
