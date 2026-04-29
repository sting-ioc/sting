package com.example.injector.outputs;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_SupplierOptionalCollectionOutputModel implements SupplierOptionalCollectionOutputModel {
  @Nullable
  private Object node1;

  private Collection<Supplier<Optional<SupplierOptionalCollectionOutputModel.MyModel>>> $sting$_getMyModelCache;

  Sting_SupplierOptionalCollectionOutputModel() {
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( SupplierOptionalCollectionOutputModel_Sting_MyModel.create() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  public Collection<Supplier<Optional<SupplierOptionalCollectionOutputModel.MyModel>>> getMyModel(
      ) {
    if ( null == $sting$_getMyModelCache ) {
      $sting$_getMyModelCache = Collections.singletonList( () -> Optional.ofNullable( (SupplierOptionalCollectionOutputModel.MyModel) node1() ) );
    }
    return $sting$_getMyModelCache;
  }
}
