package com.example.injector.outputs;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
public final class Sting_Sting_OptionalProvidesOutputModel_Provider implements Sting_OptionalProvidesOutputModel_Provider {
  @Nonnull
  public Object $sting$_provide() {
    return provide();
  }

  @Nullable
  @SuppressWarnings("unchecked")
  public Object $sting$_getMyModel(final Object injector) {
    return getMyModel( Objects.requireNonNull( (OptionalProvidesOutputModel) injector ) );
  }
}
