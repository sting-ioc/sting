package com.example.injector.includes.multiple;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_MultipleIncludesModel_Provider {
  @Nonnull
  default MultipleIncludesModel provide() {
    return new Sting_MultipleIncludesModel();
  }

  default Runnable getRunnable(final MultipleIncludesModel injector) {
    return injector.getRunnable();
  }
}
