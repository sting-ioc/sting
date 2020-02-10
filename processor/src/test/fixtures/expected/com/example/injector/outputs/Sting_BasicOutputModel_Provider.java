package com.example.injector.outputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_BasicOutputModel_Provider {
  @Nonnull
  default BasicOutputModel provide() {
    return new Sting_BasicOutputModel();
  }
}
