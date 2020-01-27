package com.example.injector.dependency;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_OptionalMissingDependencyModel implements OptionalMissingDependencyModel {
  @Nullable
  private Object node1;

  Sting_OptionalMissingDependencyModel() {
  }

  @Nonnull
  private Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( OptionalMissingDependencyModel_Sting_MyModel2.create() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  @Nullable
  public OptionalMissingDependencyModel.MyModel1 getMyModel1() {
    return null;
  }

  @Override
  public OptionalMissingDependencyModel.MyModel2 getMyModel2() {
    return (OptionalMissingDependencyModel.MyModel2) node1();
  }
}
