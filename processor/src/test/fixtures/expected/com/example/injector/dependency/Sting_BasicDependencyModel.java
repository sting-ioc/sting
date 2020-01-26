package com.example.injector.dependency;

import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_BasicDependencyModel {
  @Nullable
  private Object node1;

  private boolean $sting$_node1_allocated;

  private Sting_BasicDependencyModel() {
  }

  private Object node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = BasicDependencyModel_Sting_MyModel.create();
    }
    return node1;
  }
}
