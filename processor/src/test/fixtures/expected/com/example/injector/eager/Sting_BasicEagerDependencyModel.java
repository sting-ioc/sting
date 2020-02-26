package com.example.injector.eager;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_BasicEagerDependencyModel implements BasicEagerDependencyModel {
  @Nonnull
  private final Object node1;

  @Nullable
  private Object node2;

  @Nullable
  private Object node3;

  @Nonnull
  private final Object node4;

  @Nonnull
  private final Object node5;

  @Nonnull
  private final Object node6;

  @Nonnull
  private final Object node7;

  Sting_BasicEagerDependencyModel() {
    node1 = Objects.requireNonNull( BasicEagerDependencyModel_Sting_MyModel2.create() );
    node4 = Objects.requireNonNull( BasicEagerDependencyModel_Sting_MyModel1.create() );
    node5 = Objects.requireNonNull( BasicEagerDependencyModel_Sting_MyModel5.create(node1, () -> node3(), node4) );
    node6 = Objects.requireNonNull( BasicEagerDependencyModel_Sting_MyModel4.create(node1, () -> node3(), () -> node4) );
    node7 = Objects.requireNonNull( BasicEagerDependencyModel_Sting_MyModel6.create(node6, node5) );
  }

  @Nonnull
  @DoNotInline
  private Object node2() {
    if ( null == node2 ) {
      node2 = Objects.requireNonNull( BasicEagerDependencyModel_Sting_MyModel0.create() );
    }
    assert null != node2;
    return node2;
  }

  @Nonnull
  @DoNotInline
  private Object node3() {
    if ( null == node3 ) {
      node3 = Objects.requireNonNull( BasicEagerDependencyModel_Sting_MyModel3.create(node2(), node1) );
    }
    assert null != node3;
    return node3;
  }

  @Override
  public BasicEagerDependencyModel.MyModel6 getMyModel() {
    return (BasicEagerDependencyModel.MyModel6) node7;
  }
}
