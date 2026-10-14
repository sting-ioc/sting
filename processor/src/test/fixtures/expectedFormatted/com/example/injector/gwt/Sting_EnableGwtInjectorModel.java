package com.example.injector.gwt;

import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_EnableGwtInjectorModel implements EnableGwtInjectorModel {
  private @Nullable Object node1;

  Sting_EnableGwtInjectorModel() {}

  @DoNotInline
  private synchronized Object node1() {
    if (null == node1) {
      node1 = EnableGwtInjectorModel_Sting_MyModel0.create();
    }
    return node1;
  }

  @Override
  public EnableGwtInjectorModel.MyModel0 getMyModel() {
    return (EnableGwtInjectorModel.MyModel0) node1();
  }
}
