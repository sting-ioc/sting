package com.example.injector.inputs;

import javax.annotation.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_PrimitiveInputInjectorModel implements PrimitiveInputInjectorModel {
  private final double node1;

  private final long node2;

  private final float node3;

  private final boolean node4;

  private final short node5;

  private final int node6;

  private final char node7;

  private final byte node8;

  Sting_PrimitiveInputInjectorModel(final boolean input1, final char input2, final byte input3,
      final short input4, final int input5, final long input6, final float input7,
      final double input8) {
    node1 = input8;
    node2 = input6;
    node3 = input7;
    node4 = input1;
    node5 = input4;
    node6 = input5;
    node7 = input2;
    node8 = input3;
  }

  @Override
  public boolean value1() {
    return node4;
  }
}
