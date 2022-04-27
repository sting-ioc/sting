package com.example.injector.includes.single;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_SingleIncludesModel implements SingleIncludesModel {
  @Nonnull
  private final Sting_MyFragment fragment1 = new Sting_MyFragment();

  @Nullable
  private Runnable node1;

  Sting_SingleIncludesModel() {
  }

  @Nonnull
  @DoNotInline
  private synchronized Runnable node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( fragment1.$sting$_provideRunnable() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  public Runnable getRunnable() {
    return node1();
  }
}
