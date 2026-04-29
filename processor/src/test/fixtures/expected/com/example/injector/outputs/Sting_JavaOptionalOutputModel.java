package com.example.injector.outputs;

import java.util.Objects;
import java.util.Optional;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_JavaOptionalOutputModel implements JavaOptionalOutputModel {
  @Nullable
  private Object node1;

  Sting_JavaOptionalOutputModel() {
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( JavaOptionalOutputModel_Sting_MyModel.create() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  public Optional<JavaOptionalOutputModel.MyModel> getMyModel() {
    return Optional.ofNullable( (JavaOptionalOutputModel.MyModel) node1() );
  }
}
