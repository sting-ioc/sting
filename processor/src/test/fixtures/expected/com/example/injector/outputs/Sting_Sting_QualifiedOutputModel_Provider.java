package com.example.injector.outputs;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_Sting_QualifiedOutputModel_Provider implements Sting_QualifiedOutputModel_Provider {
  @Nonnull
  public Object $sting$_provide() {
    return provide();
  }

  @SuppressWarnings("unchecked")
  public Object $sting$_getMyModel(final Object injector) {
    return getMyModel( Objects.requireNonNull( (QualifiedOutputModel) injector ) );
  }
}