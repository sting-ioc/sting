package com.example.injector.circular;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenDirectCircularDependencyModel_Sting_MyModel1 {
  private SupplierBrokenDirectCircularDependencyModel_Sting_MyModel1() {}

  @SuppressWarnings("unchecked")
  public static Object create(final Object model) {
    return new SupplierBrokenDirectCircularDependencyModel.MyModel1(
        (SupplierBrokenDirectCircularDependencyModel.MyModel2) model);
  }
}
