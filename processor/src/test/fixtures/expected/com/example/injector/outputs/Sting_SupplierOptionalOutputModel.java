package com.example.injector.outputs;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_SupplierOptionalOutputModel implements SupplierOptionalOutputModel {
  @Nullable
  private Object node1;

  Sting_SupplierOptionalOutputModel() {
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( SupplierOptionalOutputModel_Sting_MyModel.create() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  public Supplier<Optional<SupplierOptionalOutputModel.MyModel>> getMyModel() {
    return () -> Optional.ofNullable( (SupplierOptionalOutputModel.MyModel) node1() );
  }
}
