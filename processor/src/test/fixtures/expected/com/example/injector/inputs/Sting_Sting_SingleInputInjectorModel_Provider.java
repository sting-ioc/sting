package com.example.injector.inputs;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_Sting_SingleInputInjectorModel_Provider implements Sting_SingleInputInjectorModel_Provider {
  @Nonnull
  public Object $sting$_provide(@Nonnull final Runnable input1) {
    return provide( Objects.requireNonNull( input1 ) );
  }

  @SuppressWarnings("unchecked")
  public Runnable $sting$_getRunnable(final Object injector) {
    return getRunnable( Objects.requireNonNull( (SingleInputInjectorModel) injector ) );
  }
}
