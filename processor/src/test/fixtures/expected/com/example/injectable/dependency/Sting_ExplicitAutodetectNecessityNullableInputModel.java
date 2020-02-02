package com.example.injectable.dependency;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
public final class Sting_ExplicitAutodetectNecessityNullableInputModel {
  private Sting_ExplicitAutodetectNecessityNullableInputModel() {
  }

  @Nonnull
  public static ExplicitAutodetectNecessityNullableInputModel create(
      @Nullable final Runnable runnable) {
    return new ExplicitAutodetectNecessityNullableInputModel( runnable );
  }
}
