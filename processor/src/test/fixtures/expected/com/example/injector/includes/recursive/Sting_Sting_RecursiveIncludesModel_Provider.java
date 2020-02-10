package com.example.injector.includes.recursive;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_Sting_RecursiveIncludesModel_Provider implements Sting_RecursiveIncludesModel_Provider {
  @Nonnull
  public Object $sting$_provide() {
    return provide();
  }

  @SuppressWarnings("unchecked")
  public Runnable $sting$_getRunnable1(final Object injector) {
    return getRunnable1( Objects.requireNonNull( (RecursiveIncludesModel) injector ) );
  }

  @SuppressWarnings("unchecked")
  public Runnable $sting$_getRunnable2(final Object injector) {
    return getRunnable2( Objects.requireNonNull( (RecursiveIncludesModel) injector ) );
  }

  @SuppressWarnings("unchecked")
  public Runnable $sting$_getRunnable3(final Object injector) {
    return getRunnable3( Objects.requireNonNull( (RecursiveIncludesModel) injector ) );
  }

  @SuppressWarnings("unchecked")
  public MyModel1 $sting$_getMyModel1(final Object injector) {
    return getMyModel1( Objects.requireNonNull( (RecursiveIncludesModel) injector ) );
  }

  @SuppressWarnings("unchecked")
  public Object $sting$_getMyModel2(final Object injector) {
    return getMyModel2( Objects.requireNonNull( (RecursiveIncludesModel) injector ) );
  }

  @SuppressWarnings("unchecked")
  public Object $sting$_getMyModel3(final Object injector) {
    return getMyModel3( Objects.requireNonNull( (RecursiveIncludesModel) injector ) );
  }
}
