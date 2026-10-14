package com.example.injector.circular;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_SupplierBrokenDirectCircularDependencyModel implements SupplierBrokenDirectCircularDependencyModel {
  private @Nullable Object node1;

  private @Nullable Object node2;

  Sting_SupplierBrokenDirectCircularDependencyModel() {
  }

  private synchronized Object node1() {
    if ( null == node1 ) {
      node1 = SupplierBrokenDirectCircularDependencyModel_Sting_MyModel2.create(() -> node2());
    }
    return node1;
  }

  private synchronized Object node2() {
    if ( null == node2 ) {
      node2 = SupplierBrokenDirectCircularDependencyModel_Sting_MyModel1.create(node1());
    }
    return node2;
  }

  @Override
  public SupplierBrokenDirectCircularDependencyModel.MyModel1 getMyModel1() {
    return (SupplierBrokenDirectCircularDependencyModel.MyModel1) node2();
  }

  @Override
  public SupplierBrokenDirectCircularDependencyModel.MyModel2 getMyModel2() {
    return (SupplierBrokenDirectCircularDependencyModel.MyModel2) node1();
  }
}
