package com.example.injector.circular;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_SupplierBrokenFragmentWalkingCircularDependencyModel implements SupplierBrokenFragmentWalkingCircularDependencyModel {
  @Nonnull
  private final SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyFragment fragment1 = new SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyFragment();

  @Nullable
  private Object node1;

  @Nullable
  private Object node2;

  @Nullable
  private Object node3;

  private boolean $sting$_node1_allocated;

  private boolean $sting$_node2_allocated;

  private boolean $sting$_node3_allocated;

  private Sting_SupplierBrokenFragmentWalkingCircularDependencyModel() {
  }

  private Object node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = fragment1.$sting$_provideMyModel2(() -> (SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel1) node3());
    }
    return node1;
  }

  private Object node2() {
    if ( !$sting$_node2_allocated ) {
      $sting$_node2_allocated = true;
      node2 = SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyModel2.create((SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel3) node1());
    }
    return node2;
  }

  private Object node3() {
    if ( !$sting$_node3_allocated ) {
      $sting$_node3_allocated = true;
      node3 = SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyModel1.create((SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel2) node2());
    }
    return node3;
  }

  @Override
  public SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel1 getMyModel1() {
    return (SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel1) node3();
  }

  @Override
  public SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel2 getMyModel2() {
    return (SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel2) node2();
  }

  @Override
  public SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel3 getMyModel3() {
    return (SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel3) node1();
  }
}
