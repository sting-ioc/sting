package com.example.injector.dependency;

import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_ComplexDependencyModel {
  @Nullable
  private Object node1;

  @Nullable
  private Object node2;

  @Nullable
  private Object node3;

  private boolean $sting$_node1_allocated;

  private boolean $sting$_node2_allocated;

  private boolean $sting$_node3_allocated;

  private Sting_ComplexDependencyModel() {
  }

  private Object node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = ComplexDependencyModel_Sting_MyModel1.create();
    }
    return node1;
  }

  private Object node2() {
    if ( !$sting$_node2_allocated ) {
      $sting$_node2_allocated = true;
      node2 = ComplexDependencyModel_Sting_MyModel2.create();
    }
    return node2;
  }

  private Object node3() {
    if ( !$sting$_node3_allocated ) {
      $sting$_node3_allocated = true;
      node3 = ComplexDependencyModel_Sting_MyModel3.create();
    }
    return node3;
  }
}
