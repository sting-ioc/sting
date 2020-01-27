package com.example.injector.dependency;

import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_OptionalMissingDependencyModel implements OptionalMissingDependencyModel {
  @Nullable
  private Object node1;

  private boolean $sting$_node1_allocated;

  private Sting_OptionalMissingDependencyModel() {
  }

  private Object node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = OptionalMissingDependencyModel_Sting_MyModel2.create();
    }
    return node1;
  }

  @Override
  @Nullable
  public OptionalMissingDependencyModel.MyModel1 getMyModel1() {
    return null;
  }

  @Override
  public OptionalMissingDependencyModel.MyModel2 getMyModel2() {
    return (OptionalMissingDependencyModel.MyModel2) node1();
  }
}
