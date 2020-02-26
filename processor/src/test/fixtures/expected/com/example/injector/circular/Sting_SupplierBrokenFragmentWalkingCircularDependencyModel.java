package com.example.injector.circular;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_SupplierBrokenFragmentWalkingCircularDependencyModel implements SupplierBrokenFragmentWalkingCircularDependencyModel {
  @Nonnull
  private final SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyFragment fragment1 = new SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyFragment();

  @Nullable
  private Object node1;

  @Nullable
  private Object node2;

  @Nullable
  private Object node3;

  Sting_SupplierBrokenFragmentWalkingCircularDependencyModel() {
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( fragment1.$sting$_provideMyModel2(() -> node3()) );
    }
    assert null != node1;
    return node1;
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node2() {
    if ( null == node2 ) {
      node2 = Objects.requireNonNull( SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyModel2.create(node1()) );
    }
    assert null != node2;
    return node2;
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node3() {
    if ( null == node3 ) {
      node3 = Objects.requireNonNull( SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyModel1.create(node2()) );
    }
    assert null != node3;
    return node3;
  }

  @Override
  public SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel1 getMyModel1() {
    return (SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel1) node3();
  }

  @Override
  public SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel2 getMyModel2() {
    return (SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel2) node2();
  }

  @Override
  public SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel3 getMyModel3() {
    return (SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel3) node1();
  }
}
