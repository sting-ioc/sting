package com.example.injector.includes.provider.naming.flat_enclosing;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_MyInjector implements MyInjector {
  private @Nullable Outer_Middle_Leaf_MyFramework_MyModel2 node1;

  private @Nullable MyFramework_MyModel node2;

  Sting_MyInjector() {}

  private synchronized Outer_Middle_Leaf_MyFramework_MyModel2 node1() {
    if (null == node1) {
      node1 = Sting_Outer_Middle_Leaf_MyFramework_MyModel2.create();
    }
    return node1;
  }

  private synchronized MyFramework_MyModel node2() {
    if (null == node2) {
      node2 = Sting_MyFramework_MyModel.create();
    }
    return node2;
  }

  @Override
  public MyModel getMyModel() {
    return node2();
  }

  @Override
  public Outer.Middle.Leaf.MyModel2 getMyModel2() {
    return node1();
  }
}
