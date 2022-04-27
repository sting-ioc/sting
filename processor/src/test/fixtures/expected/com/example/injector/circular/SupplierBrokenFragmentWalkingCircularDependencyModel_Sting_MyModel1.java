package com.example.injector.circular;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyModel1 {
  private SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyModel1() {
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public static Object create(final Object model) {
    return new SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel1( Objects.requireNonNull( (SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel2) model ) );
  }
}
