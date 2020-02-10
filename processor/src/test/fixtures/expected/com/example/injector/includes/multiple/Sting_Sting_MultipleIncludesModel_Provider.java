package com.example.injector.includes.multiple;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_Sting_MultipleIncludesModel_Provider implements Sting_MultipleIncludesModel_Provider {
  @Nonnull
  public Object $sting$_provide() {
    return provide();
  }

  @SuppressWarnings("unchecked")
  public Runnable $sting$_getRunnable(final Object injector) {
    return getRunnable( Objects.requireNonNull( (MultipleIncludesModel) injector ) );
  }
}
