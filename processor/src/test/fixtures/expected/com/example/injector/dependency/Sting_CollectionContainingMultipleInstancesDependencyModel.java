package com.example.injector.dependency;

import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_CollectionContainingMultipleInstancesDependencyModel implements CollectionContainingMultipleInstancesDependencyModel {
  @Nonnull
  private final CollectionContainingMultipleInstancesDependencyModel_Sting_MyFragment1 fragment1 = new CollectionContainingMultipleInstancesDependencyModel_Sting_MyFragment1();

  @Nonnull
  private final CollectionContainingMultipleInstancesDependencyModel_Sting_MyFragment2 fragment2 = new CollectionContainingMultipleInstancesDependencyModel_Sting_MyFragment2();

  @Nonnull
  private final CollectionContainingMultipleInstancesDependencyModel_Sting_MyFragment3 fragment3 = new CollectionContainingMultipleInstancesDependencyModel_Sting_MyFragment3();

  @Nullable
  private Object node1;

  @Nullable
  private Object node2;

  @Nullable
  private Object node3;

  private boolean $sting$_node1_allocated;

  private boolean $sting$_node2_allocated;

  private boolean $sting$_node3_allocated;

  Sting_CollectionContainingMultipleInstancesDependencyModel() {
  }

  private Object node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = fragment3.$sting$_myModel();
    }
    return node1;
  }

  private Object node2() {
    if ( !$sting$_node2_allocated ) {
      $sting$_node2_allocated = true;
      node2 = fragment2.$sting$_myModel();
    }
    return node2;
  }

  private Object node3() {
    if ( !$sting$_node3_allocated ) {
      $sting$_node3_allocated = true;
      node3 = fragment1.$sting$_myModel();
    }
    return node3;
  }

  @Override
  public Collection<CollectionContainingMultipleInstancesDependencyModel.MyModel> getMyModel() {
    return Arrays.asList( (CollectionContainingMultipleInstancesDependencyModel.MyModel) node3(), (CollectionContainingMultipleInstancesDependencyModel.MyModel) node2(), (CollectionContainingMultipleInstancesDependencyModel.MyModel) node1() );
  }
}
