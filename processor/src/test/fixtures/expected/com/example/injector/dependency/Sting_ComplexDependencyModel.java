package com.example.injector.dependency;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_ComplexDependencyModel implements ComplexDependencyModel {
  @Nullable
  private Object node1;

  @Nullable
  private Object node2;

  @Nullable
  private Object node3;

  Sting_ComplexDependencyModel() {
  }

  @Nonnull
  private Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( ComplexDependencyModel_Sting_MyModel3.create() );
    }
    assert null != node1;
    return node1;
  }

  @Nonnull
  private Object node2() {
    if ( null == node2 ) {
      node2 = Objects.requireNonNull( ComplexDependencyModel_Sting_MyModel2.create() );
    }
    assert null != node2;
    return node2;
  }

  @Nonnull
  private Object node3() {
    if ( null == node3 ) {
      node3 = Objects.requireNonNull( ComplexDependencyModel_Sting_MyModel1.create() );
    }
    assert null != node3;
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
