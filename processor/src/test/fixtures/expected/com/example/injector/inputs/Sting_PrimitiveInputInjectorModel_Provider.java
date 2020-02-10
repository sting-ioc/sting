package com.example.injector.inputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_PrimitiveInputInjectorModel_Provider {
  @Nonnull
  default PrimitiveInputInjectorModel provide(final boolean input1, final char input2,
      final byte input3, final short input4, final int input5, final long input6,
      final float input7, final double input8) {
    return new Sting_PrimitiveInputInjectorModel(input1, input2, input3, input4, input5, input6, input7, input8);
  }
}
