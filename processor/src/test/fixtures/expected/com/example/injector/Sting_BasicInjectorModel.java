package com.example.injector;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_BasicInjectorModel implements BasicInjectorModel {
  @Nullable
  private Object node1;

  Sting_BasicInjectorModel() {
  }

  @Nonnull
  @DoNotInline
  private Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( BasicInjectorModel_Sting_MyModel.create() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  public BasicInjectorModel.MyModel getMyModel() {
    return (BasicInjectorModel.MyModel) node1();
  }
}
