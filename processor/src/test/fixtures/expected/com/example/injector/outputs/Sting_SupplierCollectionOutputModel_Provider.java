package com.example.injector.outputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_SupplierCollectionOutputModel_Provider {
  @Nonnull
  default SupplierCollectionOutputModel provide() {
    return new Sting_SupplierCollectionOutputModel();
  }
}
