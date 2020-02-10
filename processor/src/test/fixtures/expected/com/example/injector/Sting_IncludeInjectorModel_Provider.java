package com.example.injector;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_IncludeInjectorModel_Provider {
  @Nonnull
  default IncludeInjectorModel provide() {
    return new Sting_IncludeInjectorModel();
  }

  default IncludeInjectorModel.MyModel getMyModel(final IncludeInjectorModel injector) {
    return injector.getMyModel();
  }
}
