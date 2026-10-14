package com.example.injector.includes.single;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_SingleIncludesModel implements SingleIncludesModel {
  private final Sting_MyFragment fragment1 = new Sting_MyFragment();

  private @Nullable Runnable node1;

  Sting_SingleIncludesModel() {
  }

  private synchronized Runnable node1() {
    if ( null == node1 ) {
      node1 = fragment1.$sting$_provideRunnable();
    }
    return node1;
  }

  @Override
  public Runnable getRunnable() {
    return node1();
  }
}
