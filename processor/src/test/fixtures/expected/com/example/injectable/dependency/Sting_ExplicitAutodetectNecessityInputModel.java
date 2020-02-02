package com.example.injectable.dependency;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_ExplicitAutodetectNecessityInputModel {
  private Sting_ExplicitAutodetectNecessityInputModel() {
  }

  @Nonnull
  public static ExplicitAutodetectNecessityInputModel create(final Runnable runnable) {
    return new ExplicitAutodetectNecessityInputModel( Objects.requireNonNull( runnable ) );
  }
}
