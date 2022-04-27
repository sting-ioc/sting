package com.example.injector.circular;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenDirectCircularDependencyModel_Sting_MyModel2 {
  private SupplierBrokenDirectCircularDependencyModel_Sting_MyModel2() {
  }

  @Nonnull
  @SuppressWarnings({
      "rawtypes",
      "unchecked"
  })
  public static Object create(final Supplier model) {
    return new SupplierBrokenDirectCircularDependencyModel.MyModel2( Objects.requireNonNull( (Supplier<SupplierBrokenDirectCircularDependencyModel.MyModel1>) model ) );
  }
}
