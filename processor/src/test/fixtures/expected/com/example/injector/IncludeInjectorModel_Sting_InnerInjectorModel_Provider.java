package com.example.injector;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface IncludeInjectorModel_Sting_InnerInjectorModel_Provider {
  @Nonnull
  default IncludeInjectorModel.InnerInjectorModel provide() {
    return new IncludeInjectorModel_Sting_InnerInjectorModel();
  }

  default IncludeInjectorModel.MyModel getMyModel(
      final IncludeInjectorModel.InnerInjectorModel injector) {
    return injector.getMyModel();
  }
}
