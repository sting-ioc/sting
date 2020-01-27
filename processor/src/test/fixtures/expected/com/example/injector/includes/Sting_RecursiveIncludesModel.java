package com.example.injector.includes;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_RecursiveIncludesModel implements RecursiveIncludesModel {
  @Nonnull
  private final RecursiveIncludesModel_Sting_MyFragment1 fragment1 = new RecursiveIncludesModel_Sting_MyFragment1();

  @Nonnull
  private final RecursiveIncludesModel_Sting_MyFragment2 fragment2 = new RecursiveIncludesModel_Sting_MyFragment2();

  @Nonnull
  private final RecursiveIncludesModel_Sting_MyFragment3 fragment3 = new RecursiveIncludesModel_Sting_MyFragment3();

  @Nullable
  private Object node1;

  @Nullable
  private Object node2;

  @Nullable
  private Object node3;

  @Nullable
  private Runnable node4;

  @Nullable
  private Runnable node5;

  @Nullable
  private Runnable node6;

  private boolean $sting$_node1_allocated;

  private boolean $sting$_node2_allocated;

  private boolean $sting$_node3_allocated;

  private boolean $sting$_node4_allocated;

  private boolean $sting$_node5_allocated;

  private boolean $sting$_node6_allocated;

  Sting_RecursiveIncludesModel() {
  }

  private Object node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = RecursiveIncludesModel_Sting_MyModel3.create();
    }
    return node1;
  }

  private Object node2() {
    if ( !$sting$_node2_allocated ) {
      $sting$_node2_allocated = true;
      node2 = RecursiveIncludesModel_Sting_MyModel2.create();
    }
    return node2;
  }

  private Object node3() {
    if ( !$sting$_node3_allocated ) {
      $sting$_node3_allocated = true;
      node3 = RecursiveIncludesModel_Sting_MyModel1.create();
    }
    return node3;
  }

  private Runnable node4() {
    if ( !$sting$_node4_allocated ) {
      $sting$_node4_allocated = true;
      node4 = fragment3.$sting$_provideRunnable();
    }
    return node4;
  }

  private Runnable node5() {
    if ( !$sting$_node5_allocated ) {
      $sting$_node5_allocated = true;
      node5 = fragment2.$sting$_provideRunnable();
    }
    return node5;
  }

  private Runnable node6() {
    if ( !$sting$_node6_allocated ) {
      $sting$_node6_allocated = true;
      node6 = fragment1.$sting$_provideRunnable();
    }
    return node6;
  }

  @Override
  public Runnable getRunnable1() {
    return node6();
  }

  @Override
  public Runnable getRunnable2() {
    return node5();
  }

  @Override
  public Runnable getRunnable3() {
    return node4();
  }

  @Override
  public RecursiveIncludesModel.MyModel1 getMyModel1() {
    return (RecursiveIncludesModel.MyModel1) node3();
  }

  @Override
  public RecursiveIncludesModel.MyModel2 getMyModel2() {
    return (RecursiveIncludesModel.MyModel2) node2();
  }

  @Override
  public RecursiveIncludesModel.MyModel3 getMyModel3() {
    return (RecursiveIncludesModel.MyModel3) node1();
  }
}
