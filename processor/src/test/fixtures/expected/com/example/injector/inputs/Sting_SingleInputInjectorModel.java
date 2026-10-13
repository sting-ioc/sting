package com.example.injector.inputs;

import java.util.Objects;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@SuppressWarnings("UnusedVariable")
@Generated("sting.processor.StingProcessor")
final class Sting_SingleInputInjectorModel implements SingleInputInjectorModel {
  private final Runnable node1;

  Sting_SingleInputInjectorModel(final Runnable input1) {
    node1 = Objects.requireNonNull( input1 );
  }

  @Override
  public Runnable getRunnable() {
    return node1;
  }
}
