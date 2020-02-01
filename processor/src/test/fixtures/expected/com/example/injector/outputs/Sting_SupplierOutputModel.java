package com.example.injector.outputs;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_SupplierOutputModel implements SupplierOutputModel {
  @Nullable
  private Object node1;

  Sting_SupplierOutputModel() {
  }

  @Nonnull
  private Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( SupplierOutputModel_Sting_MyModel.create() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  public Supplier<SupplierOutputModel.MyModel> getMyModel() {
    return () -> (SupplierOutputModel.MyModel) node1();
  }
}
