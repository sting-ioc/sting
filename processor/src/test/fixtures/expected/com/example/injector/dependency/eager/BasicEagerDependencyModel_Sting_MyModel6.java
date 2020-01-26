package com.example.injector.dependency.eager;

import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class BasicEagerDependencyModel_Sting_MyModel6 {
  private BasicEagerDependencyModel_Sting_MyModel6() {
  }

  @Nonnull
  public static BasicEagerDependencyModel.MyModel6 create(
      final BasicEagerDependencyModel.MyModel4 modelA,
      final BasicEagerDependencyModel.MyModel5 modelB) {
    return new BasicEagerDependencyModel.MyModel6( modelA, modelB );
  }
}
