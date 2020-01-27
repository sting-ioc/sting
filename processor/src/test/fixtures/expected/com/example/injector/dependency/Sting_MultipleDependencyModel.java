package com.example.injector.dependency;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_MultipleDependencyModel implements MultipleDependencyModel {
  @Nullable
  private Object node1;

  @Nullable
  private Object node2;

  Sting_MultipleDependencyModel() {
  }

  @Nonnull
  private Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( MultipleDependencyModel_Sting_MyModel2.create() );
    }
    assert null != node1;
    return node1;
  }

  @Nonnull
  private Object node2() {
    if ( null == node2 ) {
      node2 = Objects.requireNonNull( MultipleDependencyModel_Sting_MyModel1.create() );
    }
    assert null != node2;
    return node2;
  }

  @Override
  public MultipleDependencyModel.MyModel1 getMyModel() {
    return (MultipleDependencyModel.MyModel1) node2();
  }

  @Override
  public Supplier<MultipleDependencyModel.MyModel2> getMyModelSupplier() {
    return () -> (MultipleDependencyModel.MyModel2) node1();
  }
}
