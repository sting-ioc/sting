package com.example.injector.circular;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenChainedCircularDependencyModel_Sting_MyModel1 {
  private SupplierBrokenChainedCircularDependencyModel_Sting_MyModel1() {
  }

  @SuppressWarnings("unchecked")
  public static Object create(final Object model) {
    return new SupplierBrokenChainedCircularDependencyModel.MyModel1( (SupplierBrokenChainedCircularDependencyModel.MyModel2) model );
  }
}
