package com.example.injector.dependency;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_OptionalDependencyModel implements OptionalDependencyModel {
  @Nullable
  private Object node1;

  Sting_OptionalDependencyModel() {
  }

  @Nonnull
  private Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( OptionalDependencyModel_Sting_MyModel.create() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  @Nullable
  public OptionalDependencyModel.MyModel getMyModel() {
    return (OptionalDependencyModel.MyModel) node1();
  }
}
