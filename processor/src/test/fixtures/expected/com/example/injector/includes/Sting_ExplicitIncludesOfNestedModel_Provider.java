package com.example.injector.includes;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_ExplicitIncludesOfNestedModel_Provider {
  @Nonnull
  default ExplicitIncludesOfNestedModel provide() {
    return new Sting_ExplicitIncludesOfNestedModel();
  }

  default Runnable getRunnable(final ExplicitIncludesOfNestedModel injector) {
    return injector.getRunnable();
  }

  default ExplicitIncludesOfNestedModel.MyModel getMyModel(
      final ExplicitIncludesOfNestedModel injector) {
    return injector.getMyModel();
  }
}
