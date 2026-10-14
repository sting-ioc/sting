package com.example.injector.includes.provider.naming.enclosing;

import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_MyInjector implements MyInjector {
  private Outer.Middle.Leaf. @Nullable MyFramework_MyModel2 node1;

  private @Nullable MyFramework_MyModel node2;

  Sting_MyInjector() {
  }

  @DoNotInline
  private synchronized Outer.Middle.Leaf.MyFramework_MyModel2 node1() {
    if ( null == node1 ) {
      node1 = Outer_Middle_Leaf_Sting_MyFramework_MyModel2.create();
    }
    return node1;
  }

  @DoNotInline
  private synchronized MyFramework_MyModel node2() {
    if ( null == node2 ) {
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
