package com.example.injector.circular;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenChainedCircularDependencyModel_Sting_MyModel1 {
  private SupplierBrokenChainedCircularDependencyModel_Sting_MyModel1() {
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public static SupplierBrokenChainedCircularDependencyModel.MyModel1 create(final Object model) {
    return new SupplierBrokenChainedCircularDependencyModel.MyModel1( Objects.requireNonNull( (SupplierBrokenChainedCircularDependencyModel.MyModel2) model ) );
  }
}