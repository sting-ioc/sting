package com.example.injector.circular;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenDirectCircularDependencyModel_Sting_MyModel2 {
  private SupplierBrokenDirectCircularDependencyModel_Sting_MyModel2() {
  }

  @Nonnull
  @SuppressWarnings({
      "rawtypes",
      "unchecked"
  })
  public static SupplierBrokenDirectCircularDependencyModel.MyModel2 create(final Supplier model) {
    return new SupplierBrokenDirectCircularDependencyModel.MyModel2( Objects.requireNonNull( (Supplier<SupplierBrokenDirectCircularDependencyModel.MyModel1>) model ) );
  }
}
