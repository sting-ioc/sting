package com.example.injector.includes;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_SingleIncludesModel implements SingleIncludesModel {
  @Nonnull
  private final SingleIncludesModel_Sting_MyFragment fragment1 = new SingleIncludesModel_Sting_MyFragment();

  @Nullable
  private Runnable node1;

  private boolean $sting$_node1_allocated;

  private Sting_SingleIncludesModel() {
  }

  private Runnable node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = fragment1.$sting$_provideRunnable();
    }
    return node1;
  }

  @Override
  public Runnable getRunnable() {
    return node1();
  }
}
