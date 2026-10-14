package com.example.injector.includes.recursive;

import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_RecursiveIncludesModel implements RecursiveIncludesModel {
  private final Sting_MyFragment1 fragment1 = new Sting_MyFragment1();

  private final Sting_MyFragment2 fragment2 = new Sting_MyFragment2();

  private final Sting_MyFragment3 fragment3 = new Sting_MyFragment3();

  private final Object node1;

  private final MyModel1 node2;

  private @Nullable Runnable node3;

  private final Object node4;

  private @Nullable Runnable node5;

  private @Nullable Runnable node6;

  Sting_RecursiveIncludesModel() {
    node1 = Sting_MyModel3.create();
    node2 = Sting_MyModel1.create();
    node4 = Sting_MyModel2.create();
  }

  @DoNotInline
  private synchronized Runnable node3() {
    if ( null == node3 ) {
      node3 = fragment2.$sting$_provideRunnable();
    }
    return node3;
  }

  @DoNotInline
  private synchronized Runnable node5() {
    if ( null == node5 ) {
      node5 = fragment1.$sting$_provideRunnable();
    }
    return node5;
  }

  @DoNotInline
  private synchronized Runnable node6() {
    if ( null == node6 ) {
      node6 = fragment3.$sting$_provideRunnable();
    }
    return node6;
  }

  @Override
  public Runnable getRunnable1() {
    return node5();
  }

  @Override
  public Runnable getRunnable2() {
    return node3();
  }

  @Override
  public Runnable getRunnable3() {
    return node6();
  }

  @Override
  public MyModel1 getMyModel1() {
    return node2;
  }

  @Override
  public MyModel2 getMyModel2() {
    return (MyModel2) node4;
  }

  @Override
  public MyModel3 getMyModel3() {
    return (MyModel3) node1;
  }
}
