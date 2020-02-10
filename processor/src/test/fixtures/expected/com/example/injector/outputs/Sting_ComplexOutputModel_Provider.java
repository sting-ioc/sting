package com.example.injector.outputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_ComplexOutputModel_Provider {
  @Nonnull
  default ComplexOutputModel provide() {
    return new Sting_ComplexOutputModel();
  }

  default ComplexOutputModel.MyModel1 getMyModel1(final ComplexOutputModel injector) {
    return injector.getMyModel1();
  }

  @Nullable
  default ComplexOutputModel.MyModel4 getMyModel4(final ComplexOutputModel injector) {
    return injector.getMyModel4();
  }
}
