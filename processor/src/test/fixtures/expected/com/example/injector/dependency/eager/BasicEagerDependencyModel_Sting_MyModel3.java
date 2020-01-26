package com.example.injector.dependency.eager;

import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class BasicEagerDependencyModel_Sting_MyModel3 {
  private BasicEagerDependencyModel_Sting_MyModel3() {
  }

  @Nonnull
  public static BasicEagerDependencyModel.MyModel3 create(
      final BasicEagerDependencyModel.MyModel0 modelA,
      final BasicEagerDependencyModel.MyModel2 modelB) {
    return new BasicEagerDependencyModel.MyModel3( modelA, modelB );
  }
}
