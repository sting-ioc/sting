package com.example.injector.circular;

import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_SupplierBrokenChainedCircularDependencyModel implements SupplierBrokenChainedCircularDependencyModel {
  @Nullable
  private Object node1;

  @Nullable
  private Object node2;

  @Nullable
  private Object node3;

  private boolean $sting$_node1_allocated;

  private boolean $sting$_node2_allocated;

  private boolean $sting$_node3_allocated;

  private Sting_SupplierBrokenChainedCircularDependencyModel() {
  }

  private Object node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = SupplierBrokenChainedCircularDependencyModel_Sting_MyModel3.create(() -> (SupplierBrokenChainedCircularDependencyModel.MyModel1) node3());
    }
    return node1;
  }

  private Object node2() {
    if ( !$sting$_node2_allocated ) {
      $sting$_node2_allocated = true;
      node2 = SupplierBrokenChainedCircularDependencyModel_Sting_MyModel2.create((SupplierBrokenChainedCircularDependencyModel.MyModel3) node1());
    }
    return node2;
  }

  private Object node3() {
    if ( !$sting$_node3_allocated ) {
      $sting$_node3_allocated = true;
      node3 = SupplierBrokenChainedCircularDependencyModel_Sting_MyModel1.create((SupplierBrokenChainedCircularDependencyModel.MyModel2) node2());
    }
    return node3;
  }

  @Override
  public SupplierBrokenChainedCircularDependencyModel.MyModel1 getMyModel1() {
    return (SupplierBrokenChainedCircularDependencyModel.MyModel1) node3();
  }

  @Override
  public SupplierBrokenChainedCircularDependencyModel.MyModel2 getMyModel2() {
    return (SupplierBrokenChainedCircularDependencyModel.MyModel2) node2();
  }

  @Override
  public SupplierBrokenChainedCircularDependencyModel.MyModel3 getMyModel3() {
    return (SupplierBrokenChainedCircularDependencyModel.MyModel3) node1();
  }
}
