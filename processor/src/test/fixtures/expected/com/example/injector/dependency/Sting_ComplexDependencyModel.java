package com.example.injector.dependency;

import java.util.function.Supplier;
import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_ComplexDependencyModel implements ComplexDependencyModel {
  @Nullable
  private Object node1;

  @Nullable
  private Object node2;

  @Nullable
  private Object node3;

  private boolean $sting$_node1_allocated;

  private boolean $sting$_node2_allocated;

  private boolean $sting$_node3_allocated;

  Sting_ComplexDependencyModel() {
  }

  private Object node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = ComplexDependencyModel_Sting_MyModel3.create();
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
      node3 = ComplexDependencyModel_Sting_MyModel1.create();
    }
    return node3;
  }

  @Override
  public ComplexDependencyModel.MyModel1 getMyModel1() {
    return (ComplexDependencyModel.MyModel1) node3();
  }

  @Override
  @Nullable
  public Supplier<ComplexDependencyModel.MyModel2> getMyModel2Supplier() {
    return () -> (ComplexDependencyModel.MyModel2) node2();
  }

  @Override
  public Supplier<ComplexDependencyModel.MyModel3> getMyModel3Supplier() {
    return () -> (ComplexDependencyModel.MyModel3) node1();
  }

  @Override
  @Nullable
  public ComplexDependencyModel.MyModel4 getMyModel4() {
    return null;
  }
}
