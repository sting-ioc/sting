package com.example.injector.outputs;

import java.util.Objects;
import java.util.function.Supplier;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_MultipleOutputModel implements MultipleOutputModel {
  @Nullable
  private Object node1;

  @Nullable
  private Object node2;

  Sting_MultipleOutputModel() {
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( MultipleOutputModel_Sting_MyModel1.create() );
    }
    assert null != node1;
    return node1;
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node2() {
    if ( null == node2 ) {
      node2 = Objects.requireNonNull( MultipleOutputModel_Sting_MyModel2.create() );
    }
    assert null != node2;
    return node2;
  }

  @Override
  public MultipleOutputModel.MyModel1 getMyModel() {
    return (MultipleOutputModel.MyModel1) node1();
  }

  @Override
  public Supplier<MultipleOutputModel.MyModel2> getMyModelSupplier() {
    return () -> (MultipleOutputModel.MyModel2) node2();
  }
}
