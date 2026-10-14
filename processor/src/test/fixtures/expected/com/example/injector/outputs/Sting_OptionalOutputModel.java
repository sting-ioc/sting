package com.example.injector.outputs;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_OptionalOutputModel implements OptionalOutputModel {
  private @Nullable Object node1;

  Sting_OptionalOutputModel() {
  }

  private synchronized Object node1() {
    if ( null == node1 ) {
      node1 = OptionalOutputModel_Sting_MyModel.create();
    }
    return node1;
  }

  @Override
  public OptionalOutputModel. @Nullable MyModel getMyModel() {
    return (OptionalOutputModel.MyModel) node1();
  }
}
