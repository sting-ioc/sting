package com.example.injector.includes.multiple;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_MultipleIncludesModel implements MultipleIncludesModel {
  private final Sting_MyFragment fragment1 = new Sting_MyFragment();

  private @Nullable Runnable node2;

  Sting_MultipleIncludesModel() {
    Sting_MyModel.create();
  }

  private synchronized Runnable node2() {
    if ( null == node2 ) {
      node2 = fragment1.$sting$_provideRunnable();
    }
    return node2;
  }

  @Override
  public Runnable getRunnable() {
    return node2();
  }
}
