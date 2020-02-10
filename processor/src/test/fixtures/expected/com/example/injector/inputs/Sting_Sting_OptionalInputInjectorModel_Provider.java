package com.example.injector.inputs;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
public final class Sting_Sting_OptionalInputInjectorModel_Provider implements Sting_OptionalInputInjectorModel_Provider {
  @Nonnull
  public Object $sting$_provide(@Nullable final Runnable input1) {
    return provide( input1 );
  }

  @Nullable
  @SuppressWarnings("unchecked")
  public Runnable $sting$_getRunnable(final Object injector) {
    return getRunnable( Objects.requireNonNull( (OptionalInputInjectorModel) injector ) );
  }
}
