package com.example.injector.circular;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyModel1 {
  private SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyModel1() {
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public static SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel1 create(
      final Object model) {
    return new SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel1( Objects.requireNonNull( (SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel2) model ) );
  }
}
