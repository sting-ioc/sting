package com.example.injector.outputs;

import java.util.Objects;
import java.util.function.Supplier;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_ComplexOutputModel implements ComplexOutputModel {
  @Nullable
  private Object node1;

  @Nullable
  private Object node2;

  @Nullable
  private Object node3;

  Sting_ComplexOutputModel() {
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( ComplexOutputModel_Sting_MyModel1.create() );
    }
    assert null != node1;
    return node1;
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node2() {
    if ( null == node2 ) {
      node2 = Objects.requireNonNull( ComplexOutputModel_Sting_MyModel3.create() );
    }
    assert null != node2;
    return node2;
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node3() {
    if ( null == node3 ) {
      node3 = Objects.requireNonNull( ComplexOutputModel_Sting_MyModel2.create() );
    }
    assert null != node3;
    return node3;
  }

  @Override
  public ComplexOutputModel.MyModel1 getMyModel1() {
    return (ComplexOutputModel.MyModel1) node1();
  }

  @Override
  public Supplier<ComplexOutputModel.MyModel2> getMyModel2Supplier() {
    return () -> (ComplexOutputModel.MyModel2) node3();
  }

  @Override
  public Supplier<ComplexOutputModel.MyModel3> getMyModel3Supplier() {
    return () -> (ComplexOutputModel.MyModel3) node2();
  }

  @Override
  @Nullable
  public Runnable getRunnable() {
    return null;
  }
}
