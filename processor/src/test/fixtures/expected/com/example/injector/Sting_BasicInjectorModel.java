package com.example.injector;

import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_BasicInjectorModel implements BasicInjectorModel {
  @Nullable
  private Object node1;

  private boolean $sting$_node1_allocated;

  Sting_BasicInjectorModel() {
  }

  private Object node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = BasicInjectorModel_Sting_MyModel.create();
    }
    return node1;
  }

  @Override
  public BasicInjectorModel.MyModel getMyModel() {
    return (BasicInjectorModel.MyModel) node1();
  }
}
