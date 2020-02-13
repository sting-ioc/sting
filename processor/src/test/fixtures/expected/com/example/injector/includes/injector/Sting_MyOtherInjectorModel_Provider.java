package com.example.injector.includes.injector;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_MyOtherInjectorModel_Provider {
  @Nonnull
  default MyOtherInjectorModel provide() {
    return new Sting_MyOtherInjectorModel();
  }

  default MyModel getMyModel(final MyOtherInjectorModel injector) {
    return injector.getMyModel();
  }
}
