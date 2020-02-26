package com.example.injector.outputs;

import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
final class Sting_PrimitiveOutputModel implements PrimitiveOutputModel {
  @Nonnull
  private final PrimitiveOutputModel_Sting_MyFragment fragment1 = new PrimitiveOutputModel_Sting_MyFragment();

  @Nonnull
  private final PrimitiveOutputModel_Sting_MyFragment fragment2 = new PrimitiveOutputModel_Sting_MyFragment();

  @Nonnull
  private final PrimitiveOutputModel_Sting_MyFragment fragment3 = new PrimitiveOutputModel_Sting_MyFragment();

  @Nonnull
  private final PrimitiveOutputModel_Sting_MyFragment fragment4 = new PrimitiveOutputModel_Sting_MyFragment();

  @Nonnull
  private final PrimitiveOutputModel_Sting_MyFragment fragment5 = new PrimitiveOutputModel_Sting_MyFragment();

  @Nonnull
  private final PrimitiveOutputModel_Sting_MyFragment fragment6 = new PrimitiveOutputModel_Sting_MyFragment();

  @Nonnull
  private final PrimitiveOutputModel_Sting_MyFragment fragment7 = new PrimitiveOutputModel_Sting_MyFragment();

  @Nonnull
  private final PrimitiveOutputModel_Sting_MyFragment fragment8 = new PrimitiveOutputModel_Sting_MyFragment();

  private double node1;

  private boolean node1_allocated;

  private float node2;

  private boolean node2_allocated;

  private long node3;

  private boolean node3_allocated;

  private int node4;

  private boolean node4_allocated;

  private short node5;

  private boolean node5_allocated;

  private byte node6;

  private boolean node6_allocated;

  private char node7;

  private boolean node7_allocated;

  private boolean node8;

  private boolean node8_allocated;

  Sting_PrimitiveOutputModel() {
  }

  @DoNotInline
  private double node1() {
    if ( !node1_allocated ) {
      node1_allocated = true;
      node1 = fragment8.$sting$_provideValue8();
    }
    return node1;
  }

  @DoNotInline
  private float node2() {
    if ( !node2_allocated ) {
      node2_allocated = true;
      node2 = fragment8.$sting$_provideValue7();
    }
    return node2;
  }

  @DoNotInline
  private long node3() {
    if ( !node3_allocated ) {
      node3_allocated = true;
      node3 = fragment8.$sting$_provideValue6();
    }
    return node3;
  }

  @DoNotInline
  private int node4() {
    if ( !node4_allocated ) {
      node4_allocated = true;
      node4 = fragment8.$sting$_provideValue5();
    }
    return node4;
  }

  @DoNotInline
  private short node5() {
    if ( !node5_allocated ) {
      node5_allocated = true;
      node5 = fragment8.$sting$_provideValue4();
    }
    return node5;
  }

  @DoNotInline
  private byte node6() {
    if ( !node6_allocated ) {
      node6_allocated = true;
      node6 = fragment8.$sting$_provideValue3();
    }
    return node6;
  }

  @DoNotInline
  private char node7() {
    if ( !node7_allocated ) {
      node7_allocated = true;
      node7 = fragment8.$sting$_provideValue2();
    }
    return node7;
  }

  @DoNotInline
  private boolean node8() {
    if ( !node8_allocated ) {
      node8_allocated = true;
      node8 = fragment8.$sting$_provideValue();
    }
    return node8;
  }

  @Override
  public boolean getValue1() {
    return node8();
  }

  @Override
  public char getValue2() {
    return node7();
  }

  @Override
  public byte getValue3() {
    return node6();
  }

  @Override
  public short getValue4() {
    return node5();
  }

  @Override
  public int getValue5() {
    return node4();
  }

  @Override
  public long getValue6() {
    return node3();
  }

  @Override
  public float getValue7() {
    return node2();
  }

  @Override
  public double getValue8() {
    return node1();
  }
}
