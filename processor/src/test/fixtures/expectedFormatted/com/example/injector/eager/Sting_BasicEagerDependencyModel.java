package com.example.injector.eager;

import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_BasicEagerDependencyModel implements BasicEagerDependencyModel {
  private final Object node1;

  private @Nullable Object node2;

  private @Nullable Object node3;

  private final Object node4;

  private final Object node5;

  private final Object node6;

  private final Object node7;

  Sting_BasicEagerDependencyModel() {
    node1 = BasicEagerDependencyModel_Sting_MyModel2.create();
    node4 = BasicEagerDependencyModel_Sting_MyModel1.create();
    node5 = BasicEagerDependencyModel_Sting_MyModel4.create(node1, () -> node3(), () -> node4);
    node6 = BasicEagerDependencyModel_Sting_MyModel5.create(node1, () -> node3(), node4);
    node7 = BasicEagerDependencyModel_Sting_MyModel6.create(node5, node6);
  }

  @DoNotInline
  private synchronized Object node2() {
    if (null == node2) {
      node2 = BasicEagerDependencyModel_Sting_MyModel0.create();
    }
    return node2;
  }

  @DoNotInline
  private synchronized Object node3() {
    if (null == node3) {
      node3 = BasicEagerDependencyModel_Sting_MyModel3.create(node2(), node1);
    }
    return node3;
  }

  @Override
  public BasicEagerDependencyModel.MyModel6 getMyModel() {
    return (BasicEagerDependencyModel.MyModel6) node7;
  }
}
