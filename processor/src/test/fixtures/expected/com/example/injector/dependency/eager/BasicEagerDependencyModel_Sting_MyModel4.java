package com.example.injector.dependency.eager;

import java.util.function.Supplier;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class BasicEagerDependencyModel_Sting_MyModel4 {
  private BasicEagerDependencyModel_Sting_MyModel4() {
  }

  @Nonnull
  public static BasicEagerDependencyModel.MyModel4 create(
      final BasicEagerDependencyModel.MyModel2 modelA,
      final Supplier<BasicEagerDependencyModel.MyModel3> modelB,
      final Supplier<BasicEagerDependencyModel.MyModel1> modelC) {
    return new BasicEagerDependencyModel.MyModel4( modelA, modelB, modelC );
  }
}
