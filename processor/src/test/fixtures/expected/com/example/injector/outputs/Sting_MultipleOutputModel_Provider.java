package com.example.injector.outputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_MultipleOutputModel_Provider {
  @Nonnull
  default MultipleOutputModel provide() {
    return new Sting_MultipleOutputModel();
  }

  default MultipleOutputModel.MyModel1 getMyModel(final MultipleOutputModel injector) {
    return injector.getMyModel();
  }
}
