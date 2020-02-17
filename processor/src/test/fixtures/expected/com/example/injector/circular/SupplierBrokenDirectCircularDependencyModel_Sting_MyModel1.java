package com.example.injector.circular;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenDirectCircularDependencyModel_Sting_MyModel1 {
  private SupplierBrokenDirectCircularDependencyModel_Sting_MyModel1() {
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public static Object create(final Object model) {
    return new SupplierBrokenDirectCircularDependencyModel.MyModel1( Objects.requireNonNull( (SupplierBrokenDirectCircularDependencyModel.MyModel2) model ) );
  }
}
