package com.example.injector.dependency;

import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_QualifiedDependencyModel implements QualifiedDependencyModel {
  @Nullable
  private Object node1;

  private boolean $sting$_node1_allocated;

  Sting_QualifiedDependencyModel() {
  }

  private Object node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = QualifiedDependencyModel_Sting_MyModel.create();
    }
    return node1;
  }

  @Override
  public QualifiedDependencyModel.MyModel getMyModel() {
    return (QualifiedDependencyModel.MyModel) node1();
  }
}
