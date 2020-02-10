package com.example.injector.inputs;

import javax.annotation.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_PrimitiveInputInjectorModel implements PrimitiveInputInjectorModel {
  private final boolean node1;

  private final double node2;

  private final float node3;

  private final long node4;

  private final int node5;

  private final short node6;

  private final byte node7;

  private final char node8;

  Sting_PrimitiveInputInjectorModel(final boolean input1, final char input2, final byte input3,
      final short input4, final int input5, final long input6, final float input7,
      final double input8) {
    this.node1 = input1;
    this.node2 = input8;
    this.node3 = input7;
    this.node4 = input6;
    this.node5 = input5;
    this.node6 = input4;
    this.node7 = input3;
    this.node8 = input2;
  }

  @Override
  public boolean value1() {
    return node1;
  }
}
