package com.example.injector.gwt;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_DisableGwtInjectorModel implements DisableGwtInjectorModel {
  private @Nullable Object node1;

  Sting_DisableGwtInjectorModel() {
  }

  private synchronized Object node1() {
    if ( null == node1 ) {
      node1 = DisableGwtInjectorModel_Sting_MyModel0.create();
    }
    return node1;
  }

  @Override
  public DisableGwtInjectorModel.MyModel0 getMyModel() {
    return (DisableGwtInjectorModel.MyModel0) node1();
  }
}
