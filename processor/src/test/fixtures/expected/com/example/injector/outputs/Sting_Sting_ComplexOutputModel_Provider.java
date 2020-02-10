package com.example.injector.outputs;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
public final class Sting_Sting_ComplexOutputModel_Provider implements Sting_ComplexOutputModel_Provider {
  @Nonnull
  public Object $sting$_provide() {
    return provide();
  }

  @SuppressWarnings("unchecked")
  public Object $sting$_getMyModel1(final Object injector) {
    return getMyModel1( Objects.requireNonNull( (ComplexOutputModel) injector ) );
  }

  @Nullable
  @SuppressWarnings("unchecked")
  public Object $sting$_getMyModel4(final Object injector) {
    return getMyModel4( Objects.requireNonNull( (ComplexOutputModel) injector ) );
  }
}
