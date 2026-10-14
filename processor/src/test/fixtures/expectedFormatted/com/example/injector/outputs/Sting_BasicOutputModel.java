package com.example.injector.outputs;

import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_BasicOutputModel implements BasicOutputModel {
  private @Nullable Object node1;

  Sting_BasicOutputModel() {}

  @DoNotInline
  private synchronized Object node1() {
    if (null == node1) {
      node1 = BasicOutputModel_Sting_MyModel.create();
    }
    return node1;
  }

  @Override
  public BasicOutputModel.MyModel getMyModel() {
    return (BasicOutputModel.MyModel) node1();
  }
}
