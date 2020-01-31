package com.example.injector.eager;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_BasicEagerDependencyModel implements BasicEagerDependencyModel {
  @Nullable
  private Object node1;

  @Nullable
  private Object node2;

  @Nonnull
  private final Object node3;

  @Nonnull
  private final Object node4;

  @Nonnull
  private final Object node5;

  @Nonnull
  private final Object node6;

  @Nonnull
  private final Object node7;

  Sting_BasicEagerDependencyModel() {
    node3 = Objects.requireNonNull( BasicEagerDependencyModel_Sting_MyModel2.create() );
    node4 = Objects.requireNonNull( BasicEagerDependencyModel_Sting_MyModel1.create() );
    node5 = Objects.requireNonNull( BasicEagerDependencyModel_Sting_MyModel5.create(node3, () -> node2(), node4) );
    node6 = Objects.requireNonNull( BasicEagerDependencyModel_Sting_MyModel4.create(node3, () -> node2(), () -> node4) );
    node7 = Objects.requireNonNull( BasicEagerDependencyModel_Sting_MyModel6.create(node6, node5) );
  }

  @Nonnull
  private Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( BasicEagerDependencyModel_Sting_MyModel0.create() );
    }
    assert null != node1;
    return node1;
  }

  @Nonnull
  private Object node2() {
    if ( null == node2 ) {
      node2 = Objects.requireNonNull( BasicEagerDependencyModel_Sting_MyModel3.create(node1(), node3) );
    }
    assert null != node2;
    return node2;
  }

  @Override
  public BasicEagerDependencyModel.MyModel6 getMyModel() {
    return (BasicEagerDependencyModel.MyModel6) node7;
  }
}
