package com.example.injector.circular;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_SupplierBrokenChainedCircularDependencyModel implements SupplierBrokenChainedCircularDependencyModel {
  @Nullable
  private Object node1;

  @Nullable
  private Object node2;

  @Nullable
  private Object node3;

  Sting_SupplierBrokenChainedCircularDependencyModel() {
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( SupplierBrokenChainedCircularDependencyModel_Sting_MyModel2.create(node3()) );
    }
    assert null != node1;
    return node1;
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node2() {
    if ( null == node2 ) {
      node2 = Objects.requireNonNull( SupplierBrokenChainedCircularDependencyModel_Sting_MyModel1.create(node1()) );
    }
    assert null != node2;
    return node2;
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node3() {
    if ( null == node3 ) {
      node3 = Objects.requireNonNull( SupplierBrokenChainedCircularDependencyModel_Sting_MyModel3.create(() -> node2()) );
    }
    assert null != node3;
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
