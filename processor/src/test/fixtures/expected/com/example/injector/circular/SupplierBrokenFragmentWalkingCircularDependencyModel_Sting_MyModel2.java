package com.example.injector.circular;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyModel2 {
  private SupplierBrokenFragmentWalkingCircularDependencyModel_Sting_MyModel2() {
  }

  @Nonnull
  public static Object create(final Runnable model) {
    return new SupplierBrokenFragmentWalkingCircularDependencyModel.MyModel2( Objects.requireNonNull( model ) );
  }
}
