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
}
