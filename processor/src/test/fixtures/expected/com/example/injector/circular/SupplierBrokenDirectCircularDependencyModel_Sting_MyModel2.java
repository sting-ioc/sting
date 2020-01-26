package com.example.injector.circular;

import java.util.function.Supplier;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenDirectCircularDependencyModel_Sting_MyModel2 {
  private SupplierBrokenDirectCircularDependencyModel_Sting_MyModel2() {
  }

  @Nonnull
  public static SupplierBrokenDirectCircularDependencyModel.MyModel2 create(
      final Supplier<SupplierBrokenDirectCircularDependencyModel.MyModel1> model) {
    return new SupplierBrokenDirectCircularDependencyModel.MyModel2( model );
  }
}
