package com.example.injector.circular;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_Sting_SupplierBrokenChainedCircularDependencyModel_Provider implements Sting_SupplierBrokenChainedCircularDependencyModel_Provider {
  @Nonnull
  public Object $sting$_provide() {
    return provide();
  }

  @SuppressWarnings("unchecked")
  public Object $sting$_getMyModel1(final Object injector) {
    return getMyModel1( Objects.requireNonNull( (SupplierBrokenChainedCircularDependencyModel) injector ) );
  }

  @SuppressWarnings("unchecked")
  public Object $sting$_getMyModel2(final Object injector) {
    return getMyModel2( Objects.requireNonNull( (SupplierBrokenChainedCircularDependencyModel) injector ) );
  }

  @SuppressWarnings("unchecked")
  public Object $sting$_getMyModel3(final Object injector) {
    return getMyModel3( Objects.requireNonNull( (SupplierBrokenChainedCircularDependencyModel) injector ) );
  }
}
