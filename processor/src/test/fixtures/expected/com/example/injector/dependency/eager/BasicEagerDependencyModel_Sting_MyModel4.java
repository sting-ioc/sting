package com.example.injector.dependency.eager;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class BasicEagerDependencyModel_Sting_MyModel4 {
  private BasicEagerDependencyModel_Sting_MyModel4() {
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public static BasicEagerDependencyModel.MyModel4 create(final Object modelA, final Object modelB,
      final Object modelC) {
    return new BasicEagerDependencyModel.MyModel4( Objects.requireNonNull( (BasicEagerDependencyModel.MyModel2) modelA ), Objects.requireNonNull( (Supplier<BasicEagerDependencyModel.MyModel3>) modelB ), Objects.requireNonNull( (Supplier<BasicEagerDependencyModel.MyModel1>) modelC ) );
  }
}
