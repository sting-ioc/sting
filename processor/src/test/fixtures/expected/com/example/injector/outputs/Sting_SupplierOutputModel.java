package com.example.injector.outputs;

import java.util.function.Supplier;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_SupplierOutputModel implements SupplierOutputModel {
  private @Nullable Object node1;

  Sting_SupplierOutputModel() {
  }

  @DoNotInline
  private synchronized Object node1() {
    if ( null == node1 ) {
      node1 = SupplierOutputModel_Sting_MyModel.create();
    }
    return node1;
  }

  @Override
  public Supplier<SupplierOutputModel.MyModel> getMyModel() {
    return () -> (SupplierOutputModel.MyModel) node1();
  }
}
