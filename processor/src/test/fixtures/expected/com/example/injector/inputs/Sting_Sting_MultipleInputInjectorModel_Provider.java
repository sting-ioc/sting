package com.example.injector.inputs;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_Sting_MultipleInputInjectorModel_Provider implements Sting_MultipleInputInjectorModel_Provider {
  @Nonnull
  public Object $sting$_provide(@Nonnull final Runnable input1, @Nonnull final String input2) {
    return provide( Objects.requireNonNull( input1 ), Objects.requireNonNull( input2 ) );
  }
}
