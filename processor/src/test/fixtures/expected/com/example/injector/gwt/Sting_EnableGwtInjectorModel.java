package com.example.injector.gwt;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_EnableGwtInjectorModel implements EnableGwtInjectorModel {
  @Nullable
  private Object node1;

  Sting_EnableGwtInjectorModel() {
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( EnableGwtInjectorModel_Sting_MyModel0.create() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  public EnableGwtInjectorModel.MyModel0 getMyModel() {
    return (EnableGwtInjectorModel.MyModel0) node1();
  }
}
