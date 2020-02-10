package com.example.injector;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_BasicInjectorModel_Provider {
  @Nonnull
  default BasicInjectorModel provide() {
    return new Sting_BasicInjectorModel();
  }

  default BasicInjectorModel.MyModel getMyModel(final BasicInjectorModel injector) {
    return injector.getMyModel();
  }
}
