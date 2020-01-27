package com.example.injector.dependency;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_PrimitiveDependencyModel implements PrimitiveDependencyModel {
  @Nonnull
  private final PrimitiveDependencyModel_Sting_MyFragment fragment1 = new PrimitiveDependencyModel_Sting_MyFragment();

  @Nonnull
  private final PrimitiveDependencyModel_Sting_MyFragment fragment2 = new PrimitiveDependencyModel_Sting_MyFragment();

  @Nonnull
  private final PrimitiveDependencyModel_Sting_MyFragment fragment3 = new PrimitiveDependencyModel_Sting_MyFragment();

  @Nonnull
  private final PrimitiveDependencyModel_Sting_MyFragment fragment4 = new PrimitiveDependencyModel_Sting_MyFragment();

  @Nonnull
  private final PrimitiveDependencyModel_Sting_MyFragment fragment5 = new PrimitiveDependencyModel_Sting_MyFragment();

  @Nonnull
  private final PrimitiveDependencyModel_Sting_MyFragment fragment6 = new PrimitiveDependencyModel_Sting_MyFragment();

  @Nonnull
  private final PrimitiveDependencyModel_Sting_MyFragment fragment7 = new PrimitiveDependencyModel_Sting_MyFragment();

  @Nonnull
  private final PrimitiveDependencyModel_Sting_MyFragment fragment8 = new PrimitiveDependencyModel_Sting_MyFragment();

  @Nullable
  private double node1;

  @Nullable
  private float node2;

  @Nullable
  private long node3;

  @Nullable
  private int node4;

  @Nullable
  private short node5;

  @Nullable
  private byte node6;

  @Nullable
  private char node7;

  @Nullable
  private boolean node8;

  private boolean $sting$_node1_allocated;

  private boolean $sting$_node2_allocated;

  private boolean $sting$_node3_allocated;

  private boolean $sting$_node4_allocated;

  private boolean $sting$_node5_allocated;

  private boolean $sting$_node6_allocated;

  private boolean $sting$_node7_allocated;

  private boolean $sting$_node8_allocated;

  Sting_PrimitiveDependencyModel() {
  }

  private double node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = fragment8.$sting$_provideValue8();
    }
    return node1;
  }

  private float node2() {
    if ( !$sting$_node2_allocated ) {
      $sting$_node2_allocated = true;
      node2 = fragment8.$sting$_provideValue7();
    }
    return node2;
  }

  private long node3() {
    if ( !$sting$_node3_allocated ) {
      $sting$_node3_allocated = true;
      node3 = fragment8.$sting$_provideValue6();
    }
    return node3;
  }

  private int node4() {
    if ( !$sting$_node4_allocated ) {
      $sting$_node4_allocated = true;
      node4 = fragment8.$sting$_provideValue5();
    }
    return node4;
  }

  private short node5() {
    if ( !$sting$_node5_allocated ) {
      $sting$_node5_allocated = true;
      node5 = fragment8.$sting$_provideValue4();
    }
    return node5;
  }

  private byte node6() {
    if ( !$sting$_node6_allocated ) {
      $sting$_node6_allocated = true;
      node6 = fragment8.$sting$_provideValue3();
    }
    return node6;
  }

  private char node7() {
    if ( !$sting$_node7_allocated ) {
      $sting$_node7_allocated = true;
      node7 = fragment8.$sting$_provideValue2();
    }
    return node7;
  }

  private boolean node8() {
    if ( !$sting$_node8_allocated ) {
      $sting$_node8_allocated = true;
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
