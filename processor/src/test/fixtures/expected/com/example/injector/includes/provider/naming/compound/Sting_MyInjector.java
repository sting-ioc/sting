package com.example.injector.includes.provider.naming.compound;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_MyInjector implements MyInjector {
  @Nullable
  private Outer.Middle.Leaf.MyModel2Impl node1;

  @Nullable
  private Object node2;

  Sting_MyInjector() {
  }

  @Nonnull
  @DoNotInline
  private synchronized Outer.Middle.Leaf.MyModel2Impl node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( Outer_Middle_Leaf_Sting_MyModel2Impl.create() );
    }
    assert null != node1;
    return node1;
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node2() {
    if ( null == node2 ) {
      node2 = Objects.requireNonNull( Sting_MyModel1Impl.create() );
    }
    assert null != node2;
    return node2;
  }

  @Override
  public MyModel1 getMyModel1() {
    return (MyModel1) node2();
  }

  @Override
  public Outer.Middle.Leaf.MyModel2 getMyModel2() {
    return node1();
  }
}
