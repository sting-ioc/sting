package com.example.injector.outputs;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_ExplicitAutodetectNecessityOutputModel implements ExplicitAutodetectNecessityOutputModel {
  @Nullable
  private Object node1;

  Sting_ExplicitAutodetectNecessityOutputModel() {
  }

  @Nonnull
  private Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( ExplicitAutodetectNecessityOutputModel_Sting_MyModel.create() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  public ExplicitAutodetectNecessityOutputModel.MyModel getMyModel() {
    return (ExplicitAutodetectNecessityOutputModel.MyModel) node1();
  }
}
