package com.example.injector.dependency.eager;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class BasicEagerDependencyModel_Sting_MyModel6 {
  private BasicEagerDependencyModel_Sting_MyModel6() {
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public static BasicEagerDependencyModel.MyModel6 create(final Object modelA,
      final Object modelB) {
    return new BasicEagerDependencyModel.MyModel6( Objects.requireNonNull( (BasicEagerDependencyModel.MyModel4) modelA ), Objects.requireNonNull( (BasicEagerDependencyModel.MyModel5) modelB ) );
  }
}