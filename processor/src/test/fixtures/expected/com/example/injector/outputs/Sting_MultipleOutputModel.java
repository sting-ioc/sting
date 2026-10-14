package com.example.injector.outputs;

import java.util.function.Supplier;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_MultipleOutputModel implements MultipleOutputModel {
  private @Nullable Object node1;

  private @Nullable Object node2;

  Sting_MultipleOutputModel() {
  }

  @DoNotInline
  private synchronized Object node1() {
    if ( null == node1 ) {
      node1 = MultipleOutputModel_Sting_MyModel1.create();
    }
    return node1;
  }

  @DoNotInline
  private synchronized Object node2() {
    if ( null == node2 ) {
      node2 = MultipleOutputModel_Sting_MyModel2.create();
    }
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
