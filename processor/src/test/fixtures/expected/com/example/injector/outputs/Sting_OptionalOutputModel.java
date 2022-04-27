package com.example.injector.outputs;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_OptionalOutputModel implements OptionalOutputModel {
  @Nullable
  private Object node1;

  Sting_OptionalOutputModel() {
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( OptionalOutputModel_Sting_MyModel.create() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  @Nullable
  public OptionalOutputModel.MyModel getMyModel() {
    return (OptionalOutputModel.MyModel) node1();
  }
}
