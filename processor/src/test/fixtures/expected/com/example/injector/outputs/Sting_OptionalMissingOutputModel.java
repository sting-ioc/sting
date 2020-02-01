package com.example.injector.outputs;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_OptionalMissingOutputModel implements OptionalMissingOutputModel {
  @Nullable
  private OptionalMissingOutputModel.MyModel2 node1;

  Sting_OptionalMissingOutputModel() {
  }

  @Nonnull
  private OptionalMissingOutputModel.MyModel2 node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( OptionalMissingOutputModel_Sting_MyModel2.create() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  @Nullable
  public OptionalMissingOutputModel.MyModel1 getMyModel1() {
    return null;
  }

  @Override
  public OptionalMissingOutputModel.MyModel2 getMyModel2() {
    return node1();
  }
}
