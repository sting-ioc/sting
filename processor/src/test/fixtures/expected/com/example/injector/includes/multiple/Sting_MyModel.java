package com.example.injector.includes.multiple;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_MyModel {
  private Sting_MyModel() {
  }

  @Nonnull
  public static MyModel create() {
    return new MyModel();
  }
}
