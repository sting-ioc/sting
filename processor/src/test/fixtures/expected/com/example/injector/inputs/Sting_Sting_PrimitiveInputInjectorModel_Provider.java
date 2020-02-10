package com.example.injector.inputs;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_Sting_PrimitiveInputInjectorModel_Provider implements Sting_PrimitiveInputInjectorModel_Provider {
  @Nonnull
  public Object $sting$_provide(final boolean input1, final char input2, final byte input3,
      final short input4, final int input5, final long input6, final float input7,
      final double input8) {
    return provide( Objects.requireNonNull( input1 ), Objects.requireNonNull( input2 ), Objects.requireNonNull( input3 ), Objects.requireNonNull( input4 ), Objects.requireNonNull( input5 ), Objects.requireNonNull( input6 ), Objects.requireNonNull( input7 ), Objects.requireNonNull( input8 ) );
  }
}
