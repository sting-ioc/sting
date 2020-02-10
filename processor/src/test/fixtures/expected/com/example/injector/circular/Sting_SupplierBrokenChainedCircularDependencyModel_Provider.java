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
}
