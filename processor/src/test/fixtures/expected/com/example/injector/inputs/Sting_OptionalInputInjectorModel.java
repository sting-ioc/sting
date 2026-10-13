package com.example.injector.inputs;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@SuppressWarnings("UnusedVariable")
@Generated("sting.processor.StingProcessor")
final class Sting_OptionalInputInjectorModel implements OptionalInputInjectorModel {
  private final @Nullable Runnable node1;

  Sting_OptionalInputInjectorModel(final @Nullable Runnable input1) {
    node1 = input1;
  }

  @Override
  public @Nullable Runnable getRunnable() {
    return node1;
  }
}
