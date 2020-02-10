package com.example.injector.inputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_MultipleInputInjectorModel_Provider {
  @Nonnull
  default MultipleInputInjectorModel provide(@Nonnull final Runnable input1,
      @Nonnull final String input2) {
    return new Sting_MultipleInputInjectorModel(input1, input2);
  }

  default MultipleInputInjectorModel.MyModel getMyModel(final MultipleInputInjectorModel injector) {
    return injector.getMyModel();
  }

  default String getHostname(final MultipleInputInjectorModel injector) {
    return injector.getHostname();
  }
}
