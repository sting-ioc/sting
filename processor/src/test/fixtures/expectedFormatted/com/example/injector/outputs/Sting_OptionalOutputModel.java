package com.example.injector.outputs;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_OptionalOutputModel implements OptionalOutputModel {
  private @Nullable Object node1;

  Sting_OptionalOutputModel() {}

  @DoNotInline
  private synchronized Object node1() {
    if (null == node1) {
      node1 = Objects.requireNonNull(OptionalOutputModel_Sting_MyModel.create());
    }
    assert null != node1;
    return node1;
  }

  @Override
  public OptionalOutputModel.@Nullable MyModel getMyModel() {
    return (OptionalOutputModel.MyModel) node1();
  }
}
