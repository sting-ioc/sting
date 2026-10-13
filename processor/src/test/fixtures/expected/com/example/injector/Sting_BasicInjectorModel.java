package com.example.injector;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_BasicInjectorModel implements BasicInjectorModel {
  private @Nullable Object node1;

  Sting_BasicInjectorModel() {
  }

  @DoNotInline
  private synchronized Object node1() {
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
