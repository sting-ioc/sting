package com.example.injector.circular;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_SupplierBrokenFragmentWalkingCircularDependencyModel {
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
      node1 = SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyModel1.create(node2());
    }
    return node1;
  }

  private Object node2() {
    if ( !$sting$_node2_allocated ) {
      $sting$_node2_allocated = true;
      node2 = SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyModel2.create(node3());
    }
    return node2;
  }

  private Object node3() {
    if ( !$sting$_node3_allocated ) {
      $sting$_node3_allocated = true;
      node3 = fragment1.$sting$_provideMyModel2(() -> node1());
    }
    return node3;
  }
}
