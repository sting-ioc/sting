package com.example.injector.dependency;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_BasicDependencyModel implements BasicDependencyModel {
  @Nullable
  private Object node1;

  Sting_BasicDependencyModel() {
  }

  @Nonnull
  private Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( BasicDependencyModel_Sting_MyModel.create() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  public BasicDependencyModel.MyModel getMyModel() {
    return (BasicDependencyModel.MyModel) node1();
  }
}
