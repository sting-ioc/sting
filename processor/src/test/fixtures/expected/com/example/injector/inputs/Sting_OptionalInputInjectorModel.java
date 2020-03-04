package com.example.injector.inputs;

import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_OptionalInputInjectorModel implements OptionalInputInjectorModel {
  @Nullable
  private final Runnable node1;

  Sting_OptionalInputInjectorModel(@Nullable final Runnable input1) {
    node1 = input1;
  }

  @Override
  @Nullable
  public Runnable getRunnable() {
    return node1;
  }
}
