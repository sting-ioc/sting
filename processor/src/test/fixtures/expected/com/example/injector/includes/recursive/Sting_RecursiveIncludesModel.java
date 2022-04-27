package com.example.injector.includes.recursive;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_RecursiveIncludesModel implements RecursiveIncludesModel {
  @Nonnull
  private final Sting_MyFragment1 fragment1 = new Sting_MyFragment1();

  @Nonnull
  private final Sting_MyFragment2 fragment2 = new Sting_MyFragment2();

  @Nonnull
  private final Sting_MyFragment3 fragment3 = new Sting_MyFragment3();

  @Nonnull
  private final Object node1;

  @Nonnull
  private final MyModel1 node2;

  @Nullable
  private Runnable node3;

  @Nonnull
  private final Object node4;

  @Nullable
  private Runnable node5;

  @Nullable
  private Runnable node6;

  Sting_RecursiveIncludesModel() {
    node1 = Objects.requireNonNull( Sting_MyModel3.create() );
    node2 = Objects.requireNonNull( Sting_MyModel1.create() );
    node4 = Objects.requireNonNull( Sting_MyModel2.create() );
  }

  @Nonnull
  @DoNotInline
  private synchronized Runnable node3() {
    if ( null == node3 ) {
      node3 = Objects.requireNonNull( fragment2.$sting$_provideRunnable() );
    }
    assert null != node3;
    return node3;
  }

  @Nonnull
  @DoNotInline
  private synchronized Runnable node5() {
    if ( null == node5 ) {
      node5 = Objects.requireNonNull( fragment1.$sting$_provideRunnable() );
    }
    assert null != node5;
    return node5;
  }

  @Nonnull
  @DoNotInline
  private synchronized Runnable node6() {
    if ( null == node6 ) {
      node6 = Objects.requireNonNull( fragment3.$sting$_provideRunnable() );
    }
    assert null != node6;
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
