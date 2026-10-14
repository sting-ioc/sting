package com.example.injector.outputs;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_OptionalProvidesOutputModel implements OptionalProvidesOutputModel {
  private final OptionalProvidesOutputModel_Sting_MyFragment fragment1 = new OptionalProvidesOutputModel_Sting_MyFragment();

  private @Nullable Runnable node1;

  private boolean node1_allocated;

  Sting_OptionalProvidesOutputModel() {
  }

  private synchronized @Nullable Runnable node1() {
    if ( !node1_allocated ) {
      node1_allocated = true;
      node1 = fragment1.$sting$_provideRunnable();
    }
    return node1;
  }

  @Override
  public @Nullable Runnable getRunnable() {
    return node1();
  }
}
