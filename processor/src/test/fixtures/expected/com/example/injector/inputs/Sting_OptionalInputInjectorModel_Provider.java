package com.example.injector.inputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_OptionalInputInjectorModel_Provider {
  @Nonnull
  default OptionalInputInjectorModel provide(@Nullable final Runnable input1) {
    return new Sting_OptionalInputInjectorModel(input1);
  }

  @Nullable
  default Runnable getRunnable(final OptionalInputInjectorModel injector) {
    return injector.getRunnable();
  }
}
