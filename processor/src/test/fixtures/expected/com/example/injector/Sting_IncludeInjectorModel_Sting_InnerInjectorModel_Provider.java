package com.example.injector;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_IncludeInjectorModel_Sting_InnerInjectorModel_Provider implements IncludeInjectorModel_Sting_InnerInjectorModel_Provider {
  @Nonnull
  public Object $sting$_provide() {
    return provide();
  }

  @SuppressWarnings("unchecked")
  public Object $sting$_getMyModel(final Object injector) {
    return getMyModel( Objects.requireNonNull( (IncludeInjectorModel.InnerInjectorModel) injector ) );
  }
}
