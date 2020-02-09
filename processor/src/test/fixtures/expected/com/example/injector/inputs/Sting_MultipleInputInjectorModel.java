package com.example.injector.inputs;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_MultipleInputInjectorModel implements MultipleInputInjectorModel {
  @Nonnull
  private final Runnable node1;

  @Nullable
  private Object node2;

  @Nonnull
  private final String node3;

  Sting_MultipleInputInjectorModel(@Nonnull final Runnable input1, @Nonnull final String input2) {
    this.node1 = Objects.requireNonNull( input1 );
    this.node3 = Objects.requireNonNull( input2 );
  }

  @Nonnull
  private Object node2() {
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
