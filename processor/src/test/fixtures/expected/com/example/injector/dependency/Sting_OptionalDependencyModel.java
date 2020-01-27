package com.example.injector.dependency;

import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_OptionalDependencyModel implements OptionalDependencyModel {
  @Nullable
  private Object node1;

  private boolean $sting$_node1_allocated;

  Sting_OptionalDependencyModel() {
  }

  private Object node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = OptionalDependencyModel_Sting_MyModel.create();
    }
    return node1;
  }

  @Override
  @Nullable
  public OptionalDependencyModel.MyModel getMyModel() {
    return (OptionalDependencyModel.MyModel) node1();
  }
}
