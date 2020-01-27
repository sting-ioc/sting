package com.example.injector.dependency;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_SupplierDependencyModel implements SupplierDependencyModel {
  @Nullable
  private Object node1;

  Sting_SupplierDependencyModel() {
  }

  @Nonnull
  private Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( SupplierDependencyModel_Sting_MyModel.create() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  public Supplier<SupplierDependencyModel.MyModel> getMyModel() {
    return () -> (SupplierDependencyModel.MyModel) node1();
  }
}
