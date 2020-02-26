package com.example.injector.includes.provider.naming.enclosing;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_MyInjector implements MyInjector {
  @Nullable
  private Outer.Middle.Leaf.MyFramework_MyModel2 node1;

  @Nullable
  private MyFramework_MyModel node2;

  Sting_MyInjector() {
  }

  @Nonnull
  @DoNotInline
  private Outer.Middle.Leaf.MyFramework_MyModel2 node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( Outer_Middle_Leaf_Sting_MyFramework_MyModel2.create() );
    }
    assert null != node1;
    return node1;
  }

  @Nonnull
  @DoNotInline
  private MyFramework_MyModel node2() {
    if ( null == node2 ) {
      node2 = Objects.requireNonNull( Sting_MyFramework_MyModel.create() );
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
