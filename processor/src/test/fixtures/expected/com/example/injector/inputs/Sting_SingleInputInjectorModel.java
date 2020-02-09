package com.example.injector.inputs;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
final class Sting_SingleInputInjectorModel implements SingleInputInjectorModel {
  @Nonnull
  private final Runnable node1;

  Sting_SingleInputInjectorModel(@Nonnull final Runnable input1) {
    this.node1 = Objects.requireNonNull( input1 );
  }

  @Override
  public Runnable getRunnable() {
    return node1;
  }
}
