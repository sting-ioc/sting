package com.example.injector.includes.provider.naming.flat_enclosing;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@SuppressWarnings("UnusedVariable")
@Generated("sting.processor.StingProcessor")
final class Sting_MyInjector implements MyInjector {
  private @Nullable Outer_Middle_Leaf_MyFramework_MyModel2 node1;

  private @Nullable MyFramework_MyModel node2;

  Sting_MyInjector() {}

  @DoNotInline
  private synchronized Outer_Middle_Leaf_MyFramework_MyModel2 node1() {
    if (null == node1) {
      node1 = Objects.requireNonNull(Sting_Outer_Middle_Leaf_MyFramework_MyModel2.create());
    }
    assert null != node1;
    return node1;
  }

  @DoNotInline
  private synchronized MyFramework_MyModel node2() {
    if (null == node2) {
      node2 = Objects.requireNonNull(Sting_MyFramework_MyModel.create());
    }
    assert null != node2;
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
