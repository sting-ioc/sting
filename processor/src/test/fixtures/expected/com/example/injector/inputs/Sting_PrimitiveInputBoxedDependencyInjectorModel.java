package com.example.injector.inputs;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_PrimitiveInputBoxedDependencyInjectorModel implements PrimitiveInputBoxedDependencyInjectorModel {
  private final int node1;

  @Nullable
  private Object node2;

  Sting_PrimitiveInputBoxedDependencyInjectorModel(final int input1) {
    node1 = input1;
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node2() {
    if ( null == node2 ) {
      node2 = Objects.requireNonNull( PrimitiveInputBoxedDependencyInjectorModel_Sting_MyModel.create(node1) );
    }
    assert null != node2;
    return node2;
  }

  @Override
  public PrimitiveInputBoxedDependencyInjectorModel.MyModel getMyModel() {
    return (PrimitiveInputBoxedDependencyInjectorModel.MyModel) node2();
  }
}
