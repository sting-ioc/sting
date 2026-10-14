package com.example.injector.outputs;

import java.util.function.Supplier;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_ComplexOutputModel implements ComplexOutputModel {
  private @Nullable Object node1;

  private @Nullable Object node2;

  private @Nullable Object node3;

  Sting_ComplexOutputModel() {}

  @DoNotInline
  private synchronized Object node1() {
    if (null == node1) {
      node1 = ComplexOutputModel_Sting_MyModel1.create();
    }
    return node1;
  }

  @DoNotInline
  private synchronized Object node2() {
    if (null == node2) {
      node2 = ComplexOutputModel_Sting_MyModel3.create();
    }
    return node2;
  }

  @DoNotInline
  private synchronized Object node3() {
    if (null == node3) {
      node3 = ComplexOutputModel_Sting_MyModel2.create();
    }
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
  public @Nullable Runnable getRunnable() {
    return null;
  }
}
