package com.example.injector.includes.recursive;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_RecursiveIncludesModel_Provider {
  @Nonnull
  default RecursiveIncludesModel provide() {
    return new Sting_RecursiveIncludesModel();
  }

  default Runnable getRunnable1(final RecursiveIncludesModel injector) {
    return injector.getRunnable1();
  }

  default Runnable getRunnable2(final RecursiveIncludesModel injector) {
    return injector.getRunnable2();
  }

  default Runnable getRunnable3(final RecursiveIncludesModel injector) {
    return injector.getRunnable3();
  }

  default MyModel1 getMyModel1(final RecursiveIncludesModel injector) {
    return injector.getMyModel1();
  }

  default MyModel2 getMyModel2(final RecursiveIncludesModel injector) {
    return injector.getMyModel2();
  }

  default MyModel3 getMyModel3(final RecursiveIncludesModel injector) {
    return injector.getMyModel3();
  }
}
