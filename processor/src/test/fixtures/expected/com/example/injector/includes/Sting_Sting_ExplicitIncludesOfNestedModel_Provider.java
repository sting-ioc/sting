package com.example.injector.includes;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_Sting_ExplicitIncludesOfNestedModel_Provider implements Sting_ExplicitIncludesOfNestedModel_Provider {
  @Nonnull
  public Object $sting$_provide() {
    return provide();
  }

  @SuppressWarnings("unchecked")
  public Runnable $sting$_getRunnable(final Object injector) {
    return getRunnable( Objects.requireNonNull( (ExplicitIncludesOfNestedModel) injector ) );
  }

  @SuppressWarnings("unchecked")
  public Object $sting$_getMyModel(final Object injector) {
    return getMyModel( Objects.requireNonNull( (ExplicitIncludesOfNestedModel) injector ) );
  }
}
