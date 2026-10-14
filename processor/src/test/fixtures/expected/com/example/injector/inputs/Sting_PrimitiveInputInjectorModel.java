package com.example.injector.inputs;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_PrimitiveInputInjectorModel implements PrimitiveInputInjectorModel {
  private final boolean node4;

  Sting_PrimitiveInputInjectorModel(final boolean input1, final char input2, final byte input3,
      final short input4, final int input5, final long input6, final float input7,
      final double input8) {
    node4 = input1;
  }

  @Override
  public boolean value1() {
    return node4;
  }
}
