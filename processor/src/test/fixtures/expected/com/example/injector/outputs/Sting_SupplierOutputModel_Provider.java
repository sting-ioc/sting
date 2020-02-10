package com.example.injector.outputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_SupplierOutputModel_Provider {
  @Nonnull
  default SupplierOutputModel provide() {
    return new Sting_SupplierOutputModel();
  }
}
