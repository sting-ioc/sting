package com.example.injector.dependency.eager;

import java.util.function.Supplier;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class BasicEagerDependencyModel_Sting_MyModel5 {
  private BasicEagerDependencyModel_Sting_MyModel5() {
  }

  @Nonnull
  public static BasicEagerDependencyModel.MyModel5 create(
      final BasicEagerDependencyModel.MyModel2 modelA,
      final Supplier<BasicEagerDependencyModel.MyModel3> modelB,
      final BasicEagerDependencyModel.MyModel1 modelC) {
    return new BasicEagerDependencyModel.MyModel5( modelA, modelB, modelC );
  }
}
