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
  private Runnable node1;

  @Nullable
  private Runnable node2;

  @Nullable
  private Runnable node3;

  @Nullable
  private Object node4;

  @Nullable
  private Object node5;

  @Nullable
  private Object node6;

  private boolean $sting$_node1_allocated;

  private boolean $sting$_node2_allocated;

  private boolean $sting$_node3_allocated;

  private boolean $sting$_node4_allocated;

  private boolean $sting$_node5_allocated;

  private boolean $sting$_node6_allocated;

  private Sting_RecursiveIncludesModel() {
  }

  private Runnable node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = fragment1.$sting$_provideRunnable();
    }
    return node1;
  }

  private Runnable node2() {
    if ( !$sting$_node2_allocated ) {
      $sting$_node2_allocated = true;
      node2 = fragment2.$sting$_provideRunnable();
    }
    return node2;
  }

  private Runnable node3() {
    if ( !$sting$_node3_allocated ) {
      $sting$_node3_allocated = true;
      node3 = fragment3.$sting$_provideRunnable();
    }
    return node3;
  }

  private Object node4() {
    if ( !$sting$_node4_allocated ) {
      $sting$_node4_allocated = true;
      node4 = RecursiveIncludesModel_Sting_MyModel1.create();
    }
    return node4;
  }

  private Object node5() {
    if ( !$sting$_node5_allocated ) {
      $sting$_node5_allocated = true;
      node5 = RecursiveIncludesModel_Sting_MyModel2.create();
    }
    return node5;
  }

  private Object node6() {
    if ( !$sting$_node6_allocated ) {
      $sting$_node6_allocated = true;
      node6 = RecursiveIncludesModel_Sting_MyModel3.create();
    }
    return node6;
  }

  @Override
  public Runnable getRunnable1() {
    return node1();
  }

  @Override
  public Runnable getRunnable2() {
    return node2();
  }

  @Override
  public Runnable getRunnable3() {
    return node3();
  }

  @Override
  public RecursiveIncludesModel.MyModel1 getMyModel1() {
    return (RecursiveIncludesModel.MyModel1) node4();
  }

  @Override
  public RecursiveIncludesModel.MyModel2 getMyModel2() {
    return (RecursiveIncludesModel.MyModel2) node5();
  }

  @Override
  public RecursiveIncludesModel.MyModel3 getMyModel3() {
    return (RecursiveIncludesModel.MyModel3) node6();
  }
}
