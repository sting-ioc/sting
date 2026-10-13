package com.example.injector.outputs;

import java.util.Objects;
import java.util.Optional;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@SuppressWarnings("UnusedVariable")
@Generated("sting.processor.StingProcessor")
final class Sting_JavaOptionalOutputModel implements JavaOptionalOutputModel {
  private @Nullable Object node1;

  Sting_JavaOptionalOutputModel() {}

  @DoNotInline
  private synchronized Object node1() {
    if (null == node1) {
      node1 = Objects.requireNonNull(JavaOptionalOutputModel_Sting_MyModel.create());
    }
    assert null != node1;
    return node1;
  }

  @Override
  public Optional<JavaOptionalOutputModel.MyModel> getMyModel() {
    return Optional.ofNullable((JavaOptionalOutputModel.MyModel) node1());
  }
}
