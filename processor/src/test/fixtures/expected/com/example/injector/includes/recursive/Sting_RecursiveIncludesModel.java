package com.example.injector.includes.recursive;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_RecursiveIncludesModel implements RecursiveIncludesModel {
  @Nonnull
  private final Sting_MyFragment1 fragment1 = new Sting_MyFragment1();

  @Nonnull
  private final Sting_MyFragment2 fragment2 = new Sting_MyFragment2();

  @Nonnull
  private final Sting_MyFragment3 fragment3 = new Sting_MyFragment3();

  @Nullable
  private Object node1;

  @Nullable
  private Object node2;

  @Nullable
  private MyModel1 node3;

  @Nullable
  private Runnable node4;

  @Nullable
  private Runnable node5;

  @Nullable
  private Runnable node6;

  Sting_RecursiveIncludesModel() {
  }

  @Nonnull
  @DoNotInline
  private Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( Sting_MyModel3.create() );
    }
    assert null != node1;
    return node1;
  }

  @Nonnull
  @DoNotInline
  private Object node2() {
    if ( null == node2 ) {
      node2 = Objects.requireNonNull( Sting_MyModel2.create() );
    }
    assert null != node2;
    return node2;
  }

  @Nonnull
  @DoNotInline
  private MyModel1 node3() {
    if ( null == node3 ) {
      node3 = Objects.requireNonNull( Sting_MyModel1.create() );
    }
    assert null != node3;
    return node3;
  }

  @Nonnull
  @DoNotInline
  private Runnable node4() {
    if ( null == node4 ) {
      node4 = Objects.requireNonNull( fragment3.$sting$_provideRunnable() );
    }
    assert null != node4;
    return node4;
  }

  @Nonnull
  @DoNotInline
  private Runnable node5() {
    if ( null == node5 ) {
      node5 = Objects.requireNonNull( fragment2.$sting$_provideRunnable() );
    }
    assert null != node5;
    return node5;
  }

  @Nonnull
  @DoNotInline
  private Runnable node6() {
    if ( null == node6 ) {
      node6 = Objects.requireNonNull( fragment1.$sting$_provideRunnable() );
    }
    assert null != node6;
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
  public MyModel1 getMyModel1() {
    return node3();
  }

  @Override
  public MyModel2 getMyModel2() {
    return (MyModel2) node2();
  }

  @Override
  public MyModel3 getMyModel3() {
    return (MyModel3) node1();
  }
}
