package com.example.injector.outputs;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_SupplierOptionalCollectionOutputModel
    implements SupplierOptionalCollectionOutputModel {
  private @Nullable Object node1;

  private @Nullable Collection<Supplier<Optional<SupplierOptionalCollectionOutputModel.MyModel>>>
      $sting$_getMyModelCache;

  Sting_SupplierOptionalCollectionOutputModel() {}

  @DoNotInline
  private synchronized Object node1() {
    if (null == node1) {
      node1 = SupplierOptionalCollectionOutputModel_Sting_MyModel.create();
    }
    return node1;
  }

  @Override
  public Collection<Supplier<Optional<SupplierOptionalCollectionOutputModel.MyModel>>>
      getMyModel() {
    if (null == $sting$_getMyModelCache) {
      $sting$_getMyModelCache = Collections.singletonList(
          () -> Optional.ofNullable((SupplierOptionalCollectionOutputModel.MyModel) node1()));
    }
    return $sting$_getMyModelCache;
  }
}
