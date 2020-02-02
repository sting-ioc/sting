package com.example.injector.outputs;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_ExplicitRequiredOutputModel implements ExplicitRequiredOutputModel {
  @Nullable
  private Object node1;

  Sting_ExplicitRequiredOutputModel() {
  }

  @Nonnull
  private Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( ExplicitRequiredOutputModel_Sting_MyModel.create() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  public ExplicitRequiredOutputModel.MyModel getMyModel() {
    return (ExplicitRequiredOutputModel.MyModel) node1();
  }
}
