package com.example.injectable.dependency;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_ExplicitRequiredInputModel {
  private Sting_ExplicitRequiredInputModel() {
  }

  @Nonnull
  public static ExplicitRequiredInputModel create(final Runnable runnable) {
    return new ExplicitRequiredInputModel( Objects.requireNonNull( runnable ) );
  }
}
