package com.example.injector.gwt;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_DisableGwtInjectorModel implements DisableGwtInjectorModel {
  @Nullable
  private Object node1;

  Sting_DisableGwtInjectorModel() {
  }

  @Nonnull
  private synchronized Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( DisableGwtInjectorModel_Sting_MyModel0.create() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  public DisableGwtInjectorModel.MyModel0 getMyModel() {
    return (DisableGwtInjectorModel.MyModel0) node1();
  }
}
