package com.example.injector.eager;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_BasicEagerDependencyModel_Provider {
  @Nonnull
  default BasicEagerDependencyModel provide() {
    return new Sting_BasicEagerDependencyModel();
  }

  default BasicEagerDependencyModel.MyModel6 getMyModel(final BasicEagerDependencyModel injector) {
    return injector.getMyModel();
  }
}
