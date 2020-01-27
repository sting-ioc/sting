package com.example.injector.includes;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_MultipleIncludesModel implements MultipleIncludesModel {
  @Nonnull
  private final MultipleIncludesModel_Sting_MyFragment fragment1 = new MultipleIncludesModel_Sting_MyFragment();

  @Nullable
  private Runnable node1;

  private boolean $sting$_node1_allocated;

  Sting_MultipleIncludesModel() {
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
