package com.example.injector.includes.provider.naming.compound;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_MyInjector implements MyInjector {
  private Outer.Middle.Leaf. @Nullable MyModel2Impl node1;

  private @Nullable Object node2;

  Sting_MyInjector() {
  }

  private synchronized Outer.Middle.Leaf.MyModel2Impl node1() {
    if ( null == node1 ) {
      node1 = Outer_Middle_Leaf_Sting_MyModel2Impl.create();
    }
    return node1;
  }

  private synchronized Object node2() {
    if ( null == node2 ) {
      node2 = Sting_MyModel1Impl.create();
    }
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
