package com.example.injector.dependency;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_CollectionDependencyModel implements CollectionDependencyModel {
  @Nullable
  private Object node1;

  private boolean $sting$_node1_allocated;

  Sting_CollectionDependencyModel() {
  }

  private Object node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = CollectionDependencyModel_Sting_MyModel.create();
    }
    return node1;
  }

  @Override
  public Collection<CollectionDependencyModel.MyModel> getMyModel() {
    return Collections.singletonList( (CollectionDependencyModel.MyModel) node1() );
  }
}
