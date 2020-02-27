package com.example.injector.outputs;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Supplier;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_SupplierCollectionOutputModel implements SupplierCollectionOutputModel {
  @Nullable
  private Object node1;

  private Collection<Supplier<SupplierCollectionOutputModel.MyModel>> $sting$_getMyModelCache;

  Sting_SupplierCollectionOutputModel() {
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( SupplierCollectionOutputModel_Sting_MyModel.create() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  public Collection<Supplier<SupplierCollectionOutputModel.MyModel>> getMyModel() {
    if ( null == $sting$_getMyModelCache ) {
      $sting$_getMyModelCache = Collections.singletonList( () -> (SupplierCollectionOutputModel.MyModel) node1() );
    }
    return $sting$_getMyModelCache;
  }
}
