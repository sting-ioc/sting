package com.example.injector.outputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_PrimitiveOutputModel_Provider {
  @Nonnull
  default PrimitiveOutputModel provide() {
    return new Sting_PrimitiveOutputModel();
  }
}
