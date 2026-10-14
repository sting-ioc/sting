package com.example.injector.outputs;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_PrimitiveOutputModel implements PrimitiveOutputModel {
  private final PrimitiveOutputModel_Sting_MyFragment fragment1 = new PrimitiveOutputModel_Sting_MyFragment();

  private double node1;

  private boolean node1_allocated;

  private float node2;

  private boolean node2_allocated;

  private short node3;

  private boolean node3_allocated;

  private byte node4;

  private boolean node4_allocated;

  private long node5;

  private boolean node5_allocated;

  private int node6;

  private boolean node6_allocated;

  private boolean node7;

  private boolean node7_allocated;

  private char node8;

  private boolean node8_allocated;

  Sting_PrimitiveOutputModel() {
  }

  private synchronized double node1() {
    if ( !node1_allocated ) {
      node1_allocated = true;
      node1 = fragment1.$sting$_provideValue8();
    }
    return node1;
  }

  private synchronized float node2() {
    if ( !node2_allocated ) {
      node2_allocated = true;
      node2 = fragment1.$sting$_provideValue7();
    }
    return node2;
  }

  private synchronized short node3() {
    if ( !node3_allocated ) {
      node3_allocated = true;
      node3 = fragment1.$sting$_provideValue4();
    }
    return node3;
  }

  private synchronized byte node4() {
    if ( !node4_allocated ) {
      node4_allocated = true;
      node4 = fragment1.$sting$_provideValue3();
    }
    return node4;
  }

  private synchronized long node5() {
    if ( !node5_allocated ) {
      node5_allocated = true;
      node5 = fragment1.$sting$_provideValue6();
    }
    return node5;
  }

  private synchronized int node6() {
    if ( !node6_allocated ) {
      node6_allocated = true;
      node6 = fragment1.$sting$_provideValue5();
    }
    return node6;
  }

  private synchronized boolean node7() {
    if ( !node7_allocated ) {
      node7_allocated = true;
      node7 = fragment1.$sting$_provideValue();
    }
    return node7;
  }

  private synchronized char node8() {
    if ( !node8_allocated ) {
      node8_allocated = true;
      node8 = fragment1.$sting$_provideValue2();
    }
    return node8;
  }

  @Override
  public boolean getValue1() {
    return node7();
  }

  @Override
  public char getValue2() {
    return node8();
  }

  @Override
  public byte getValue3() {
    return node4();
  }

  @Override
  public short getValue4() {
    return node3();
  }

  @Override
  public int getValue5() {
    return node6();
  }

  @Override
  public long getValue6() {
    return node5();
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
