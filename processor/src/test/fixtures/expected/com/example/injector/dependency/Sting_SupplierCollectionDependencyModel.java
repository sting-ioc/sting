package com.example.injector.dependency;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_SupplierCollectionDependencyModel implements SupplierCollectionDependencyModel {
  @Nullable
  private Object node1;

  private boolean $sting$_node1_allocated;

  private Sting_SupplierCollectionDependencyModel() {
  }

  private Object node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = SupplierCollectionDependencyModel_Sting_MyModel.create();
    }
    return node1;
  }

  @Override
  public Collection<Supplier<SupplierCollectionDependencyModel.MyModel>> getMyModel() {
    return Collections.singletonList( () -> (SupplierCollectionDependencyModel.MyModel) node1() );
  }
}
