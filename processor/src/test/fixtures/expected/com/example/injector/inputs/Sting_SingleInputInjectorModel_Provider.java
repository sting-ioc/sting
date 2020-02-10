package com.example.injector.inputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_SingleInputInjectorModel_Provider {
  @Nonnull
  default SingleInputInjectorModel provide(@Nonnull final Runnable input1) {
    return new Sting_SingleInputInjectorModel(input1);
  }

  default Runnable getRunnable(final SingleInputInjectorModel injector) {
    return injector.getRunnable();
  }
}
