package com.example.injectable.eager;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_LazyModel {
  private Sting_LazyModel() {
  }

  @Nonnull
  public static LazyModel create() {
    return new LazyModel();
  }
}
