package com.example.injector.circular;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenChainedCircularDependencyModel_Sting_MyModel2 {
  private SupplierBrokenChainedCircularDependencyModel_Sting_MyModel2() {
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public static Object create(final Object model) {
    return new SupplierBrokenChainedCircularDependencyModel.MyModel2( Objects.requireNonNull( (SupplierBrokenChainedCircularDependencyModel.MyModel3) model ) );
  }
}
