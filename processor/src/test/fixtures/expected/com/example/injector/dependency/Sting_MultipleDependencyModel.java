package com.example.injector.dependency;

import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_MultipleDependencyModel {
  @Nullable
  private Object node1;

  @Nullable
  private Object node2;

  private boolean $sting$_node1_allocated;

  private boolean $sting$_node2_allocated;

  private Sting_MultipleDependencyModel() {
  }

  private Object node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = MultipleDependencyModel_Sting_MyModel1.create();
    }
    return node1;
  }

  private Object node2() {
    if ( !$sting$_node2_allocated ) {
      $sting$_node2_allocated = true;
      node2 = MultipleDependencyModel_Sting_MyModel2.create();
    }
    return node2;
  }
}
