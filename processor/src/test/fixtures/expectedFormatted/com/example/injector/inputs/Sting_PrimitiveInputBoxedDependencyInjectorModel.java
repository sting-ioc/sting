package com.example.injector.inputs;

import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_PrimitiveInputBoxedDependencyInjectorModel
    implements PrimitiveInputBoxedDependencyInjectorModel {
  private final int node1;

  private @Nullable Object node2;

  Sting_PrimitiveInputBoxedDependencyInjectorModel(final int input1) {
    node1 = input1;
  }

  @DoNotInline
  private synchronized Object node2() {
    if (null == node2) {
      node2 = PrimitiveInputBoxedDependencyInjectorModel_Sting_MyModel.create(node1);
    }
    return node2;
  }

  @Override
  public PrimitiveInputBoxedDependencyInjectorModel.MyModel getMyModel() {
    return (PrimitiveInputBoxedDependencyInjectorModel.MyModel) node2();
  }
}
