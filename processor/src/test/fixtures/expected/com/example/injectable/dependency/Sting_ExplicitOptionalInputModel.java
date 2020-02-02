package com.example.injectable.dependency;

import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_ExplicitOptionalInputModel {
  private Sting_ExplicitOptionalInputModel() {
  }

  @Nonnull
  public static ExplicitOptionalInputModel create(final Runnable runnable) {
    return new ExplicitOptionalInputModel( runnable );
  }
}
