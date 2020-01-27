package com.example.injector.dependency;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_SupplierCollectionDependencyModel implements SupplierCollectionDependencyModel {
  @Nullable
  private Object node1;

  Sting_SupplierCollectionDependencyModel() {
  }

  @Nonnull
  private Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( SupplierCollectionDependencyModel_Sting_MyModel.create() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  public Collection<Supplier<SupplierCollectionDependencyModel.MyModel>> getMyModel() {
    return Collections.singletonList( () -> (SupplierCollectionDependencyModel.MyModel) node1() );
  }
}
