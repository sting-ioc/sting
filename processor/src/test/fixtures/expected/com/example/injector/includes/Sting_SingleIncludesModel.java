package com.example.injector.includes;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_SingleIncludesModel implements SingleIncludesModel {
  @Nonnull
  private final SingleIncludesModel_Sting_MyFragment fragment1 = new SingleIncludesModel_Sting_MyFragment();

  @Nullable
  private Runnable node1;

  Sting_SingleIncludesModel() {
  }

  @Nonnull
  private Runnable node1() {
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
