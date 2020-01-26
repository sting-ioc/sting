package com.example.injector.circular;

import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_SupplierBrokenDirectCircularDependencyModel {
  @Nullable
  private Object node1;

  @Nullable
  private Object node2;

  private boolean $sting$_node1_allocated;

  private boolean $sting$_node2_allocated;

  private Sting_SupplierBrokenDirectCircularDependencyModel() {
  }

  private Object node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = SupplierBrokenDirectCircularDependencyModel_Sting_MyModel1.create(node2());
    }
    return node1;
  }

  private Object node2() {
    if ( !$sting$_node2_allocated ) {
      $sting$_node2_allocated = true;
      node2 = SupplierBrokenDirectCircularDependencyModel_Sting_MyModel2.create(() -> node1());
    }
    return node2;
  }
}
