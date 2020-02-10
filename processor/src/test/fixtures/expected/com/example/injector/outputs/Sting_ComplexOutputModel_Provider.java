package com.example.injector.outputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_ComplexOutputModel_Provider {
  @Nonnull
  default ComplexOutputModel provide() {
    return new Sting_ComplexOutputModel();
  }
}
