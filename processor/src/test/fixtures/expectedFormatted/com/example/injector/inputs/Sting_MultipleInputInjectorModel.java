package com.example.injector.inputs;

import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_MultipleInputInjectorModel implements MultipleInputInjectorModel {
  private final Runnable node1;

  private @Nullable Object node2;

  private final String node3;

  Sting_MultipleInputInjectorModel(final Runnable input1, final String input2) {
    node1 = input1;
    node3 = input2;
  }

  @DoNotInline
  private synchronized Object node2() {
    if (null == node2) {
      node2 = MultipleInputInjectorModel_Sting_MyModel.create(node1);
    }
    return node2;
  }

  @Override
  public MultipleInputInjectorModel.MyModel getMyModel() {
    return (MultipleInputInjectorModel.MyModel) node2();
  }

  @Override
  public String getHostname() {
    return node3;
  }
}
