package com.example.injector.dependency;

import java.util.function.Supplier;
import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_SupplierDependencyModel implements SupplierDependencyModel {
  @Nullable
  private Object node1;

  private boolean $sting$_node1_allocated;

  private Sting_SupplierDependencyModel() {
  }

  private Object node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = SupplierDependencyModel_Sting_MyModel.create();
    }
    return node1;
  }

  @Override
  public Supplier<SupplierDependencyModel.MyModel> getMyModel() {
    return () -> (SupplierDependencyModel.MyModel) node1();
  }
}
