package com.example.injectable.eager;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_EagerModel {
  private Sting_EagerModel() {
  }

  @Nonnull
  public static EagerModel create() {
    return new EagerModel();
  }
}
