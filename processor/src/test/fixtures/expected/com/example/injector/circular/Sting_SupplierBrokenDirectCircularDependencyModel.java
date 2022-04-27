package com.example.injector.circular;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_SupplierBrokenDirectCircularDependencyModel implements SupplierBrokenDirectCircularDependencyModel {
  @Nullable
  private Object node1;

  @Nullable
  private Object node2;

  Sting_SupplierBrokenDirectCircularDependencyModel() {
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( SupplierBrokenDirectCircularDependencyModel_Sting_MyModel2.create(() -> node2()) );
    }
    assert null != node1;
    return node1;
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node2() {
    if ( null == node2 ) {
      node2 = Objects.requireNonNull( SupplierBrokenDirectCircularDependencyModel_Sting_MyModel1.create(node1()) );
    }
    assert null != node2;
    return node2;
  }

  @Override
  public SupplierBrokenDirectCircularDependencyModel.MyModel1 getMyModel1() {
    return (SupplierBrokenDirectCircularDependencyModel.MyModel1) node2();
  }

  @Override
  public SupplierBrokenDirectCircularDependencyModel.MyModel2 getMyModel2() {
    return (SupplierBrokenDirectCircularDependencyModel.MyModel2) node1();
  }
}
