package com.example.injector.circular;

import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_SupplierBrokenChainedCircularDependencyModel
    implements SupplierBrokenChainedCircularDependencyModel {
  private @Nullable Object node1;

  private @Nullable Object node2;

  private @Nullable Object node3;

  Sting_SupplierBrokenChainedCircularDependencyModel() {}

  @DoNotInline
  private synchronized Object node1() {
    if (null == node1) {
      node1 = SupplierBrokenChainedCircularDependencyModel_Sting_MyModel2.create(node3());
    }
    return node1;
  }

  @DoNotInline
  private synchronized Object node2() {
    if (null == node2) {
      node2 = SupplierBrokenChainedCircularDependencyModel_Sting_MyModel1.create(node1());
    }
    return node2;
  }

  @DoNotInline
  private synchronized Object node3() {
    if (null == node3) {
      node3 = SupplierBrokenChainedCircularDependencyModel_Sting_MyModel3.create(() -> node2());
    }
    return node3;
  }

  @Override
  public SupplierBrokenChainedCircularDependencyModel.MyModel1 getMyModel1() {
    return (SupplierBrokenChainedCircularDependencyModel.MyModel1) node2();
  }

  @Override
  public SupplierBrokenChainedCircularDependencyModel.MyModel2 getMyModel2() {
    return (SupplierBrokenChainedCircularDependencyModel.MyModel2) node1();
  }

  @Override
  public SupplierBrokenChainedCircularDependencyModel.MyModel3 getMyModel3() {
    return (SupplierBrokenChainedCircularDependencyModel.MyModel3) node3();
  }
}
