package com.example.injector.inputs;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_MultipleInputInjectorModel implements MultipleInputInjectorModel {
  @Nonnull
  private final Runnable node1;

  @Nullable
  private Object node2;

  @Nonnull
  private final String node3;

  Sting_MultipleInputInjectorModel(@Nonnull final Runnable input1, @Nonnull final String input2) {
    node1 = Objects.requireNonNull( input1 );
    node3 = Objects.requireNonNull( input2 );
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node2() {
    if ( null == node2 ) {
      node2 = Objects.requireNonNull( MultipleInputInjectorModel_Sting_MyModel.create(node1) );
    }
    assert null != node2;
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
