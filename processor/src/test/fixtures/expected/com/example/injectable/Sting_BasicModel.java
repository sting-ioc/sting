package com.example.injectable;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_BasicModel {
  private Sting_BasicModel() {
  }

  @Nonnull
  public static BasicModel create() {
    return new BasicModel();
  }
}
