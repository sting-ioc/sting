package com.example.injector.includes.injector;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_MyOtherInjectorModel_Provider {
  @Nonnull
  default MyOtherInjectorModel provide() {
    return new Sting_MyOtherInjectorModel();
  }

  default MyModel getMyModel(@Nonnull final MyOtherInjectorModel injector) {
    return injector.getMyModel();
  }
}
