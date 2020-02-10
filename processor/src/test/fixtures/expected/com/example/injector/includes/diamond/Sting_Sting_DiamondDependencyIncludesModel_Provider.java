package com.example.injector.includes.diamond;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_Sting_DiamondDependencyIncludesModel_Provider implements Sting_DiamondDependencyIncludesModel_Provider {
  @Nonnull
  public Object $sting$_provide() {
    return provide();
  }

  @SuppressWarnings("unchecked")
  public Object $sting$_getRunnable(final Object injector) {
    return getRunnable( Objects.requireNonNull( (DiamondDependencyIncludesModel) injector ) );
  }
}
