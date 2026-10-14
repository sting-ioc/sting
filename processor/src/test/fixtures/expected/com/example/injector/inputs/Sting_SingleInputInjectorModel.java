package com.example.injector.inputs;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_SingleInputInjectorModel implements SingleInputInjectorModel {
  private final Runnable node1;

  Sting_SingleInputInjectorModel(final Runnable input1) {
    node1 = input1;
  }

  @Override
  public Runnable getRunnable() {
    return node1;
  }
}
