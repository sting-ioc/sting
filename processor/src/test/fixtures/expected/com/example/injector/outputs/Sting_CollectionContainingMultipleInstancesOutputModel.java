package com.example.injector.outputs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_CollectionContainingMultipleInstancesOutputModel implements CollectionContainingMultipleInstancesOutputModel {
  @Nonnull
  private final CollectionContainingMultipleInstancesOutputModel_Sting_MyFragment1 fragment1 = new CollectionContainingMultipleInstancesOutputModel_Sting_MyFragment1();

  @Nonnull
  private final CollectionContainingMultipleInstancesOutputModel_Sting_MyFragment2 fragment2 = new CollectionContainingMultipleInstancesOutputModel_Sting_MyFragment2();

  @Nonnull
  private final CollectionContainingMultipleInstancesOutputModel_Sting_MyFragment3 fragment3 = new CollectionContainingMultipleInstancesOutputModel_Sting_MyFragment3();

  @Nullable
  private Object node1;

  @Nullable
  private Object node2;

  @Nullable
  private Object node3;

  Sting_CollectionContainingMultipleInstancesOutputModel() {
  }

  @Nonnull
  private Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( fragment3.$sting$_myModel() );
    }
    assert null != node1;
    return node1;
  }

  @Nonnull
  private Object node2() {
    if ( null == node2 ) {
      node2 = Objects.requireNonNull( fragment2.$sting$_myModel() );
    }
    assert null != node2;
    return node2;
  }

  @Nonnull
  private Object node3() {
    if ( null == node3 ) {
      node3 = Objects.requireNonNull( fragment1.$sting$_myModel() );
    }
    assert null != node3;
    return node3;
  }

  @Override
  public Collection<CollectionContainingMultipleInstancesOutputModel.MyModel> getMyModel() {
    return Arrays.asList( (CollectionContainingMultipleInstancesOutputModel.MyModel) node3(), (CollectionContainingMultipleInstancesOutputModel.MyModel) node2(), (CollectionContainingMultipleInstancesOutputModel.MyModel) node1() );
  }
}