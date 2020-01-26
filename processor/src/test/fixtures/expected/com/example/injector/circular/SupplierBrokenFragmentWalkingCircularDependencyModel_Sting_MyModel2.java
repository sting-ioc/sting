package com.example.injector.circular;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyModel2 {
  private SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyModel2() {
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public static SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel2 create(
      final Object model) {
    return new SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel2( Objects.requireNonNull( (SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel3) model ) );
  }
}
