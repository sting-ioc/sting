package com.example.injector.outputs;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_OptionalMissingOutputModel implements OptionalMissingOutputModel {
  @Nullable
  private OptionalMissingOutputModel.MyModel2 node1;

  Sting_OptionalMissingOutputModel() {
  }

  @Nonnull
  @DoNotInline
  private synchronized OptionalMissingOutputModel.MyModel2 node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( OptionalMissingOutputModel_Sting_MyModel2.create() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  @Nullable
  public Runnable getRunnable() {
    return null;
  }

  @Override
  public OptionalMissingOutputModel.MyModel2 getMyModel2() {
    return node1();
  }
}
