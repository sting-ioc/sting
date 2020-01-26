package com.example.injector.dependency;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_PrimitiveDependencyModel {
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
  private boolean node1;

  @Nullable
  private char node2;

  @Nullable
  private byte node3;

  @Nullable
  private short node4;

  @Nullable
  private int node5;

  @Nullable
  private long node6;

  @Nullable
  private float node7;

  @Nullable
  private double node8;

  private boolean $sting$_node1_allocated;

  private boolean $sting$_node2_allocated;

  private boolean $sting$_node3_allocated;

  private boolean $sting$_node4_allocated;

  private boolean $sting$_node5_allocated;

  private boolean $sting$_node6_allocated;

  private boolean $sting$_node7_allocated;

  private boolean $sting$_node8_allocated;

  private Sting_PrimitiveDependencyModel() {
  }

  private boolean node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = fragment8.$sting$_provideValue();
    }
    return node1;
  }

  private char node2() {
    if ( !$sting$_node2_allocated ) {
      $sting$_node2_allocated = true;
      node2 = fragment8.$sting$_provideValue2();
    }
    return node2;
  }

  private byte node3() {
    if ( !$sting$_node3_allocated ) {
      $sting$_node3_allocated = true;
      node3 = fragment8.$sting$_provideValue3();
    }
    return node3;
  }

  private short node4() {
    if ( !$sting$_node4_allocated ) {
      $sting$_node4_allocated = true;
      node4 = fragment8.$sting$_provideValue4();
    }
    return node4;
  }

  private int node5() {
    if ( !$sting$_node5_allocated ) {
      $sting$_node5_allocated = true;
      node5 = fragment8.$sting$_provideValue5();
    }
    return node5;
  }

  private long node6() {
    if ( !$sting$_node6_allocated ) {
      $sting$_node6_allocated = true;
      node6 = fragment8.$sting$_provideValue6();
    }
    return node6;
  }

  private float node7() {
    if ( !$sting$_node7_allocated ) {
      $sting$_node7_allocated = true;
      node7 = fragment8.$sting$_provideValue7();
    }
    return node7;
  }

  private double node8() {
    if ( !$sting$_node8_allocated ) {
      $sting$_node8_allocated = true;
      node8 = fragment8.$sting$_provideValue8();
    }
    return node8;
  }
}
