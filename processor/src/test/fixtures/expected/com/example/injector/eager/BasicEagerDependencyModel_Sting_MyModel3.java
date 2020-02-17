package com.example.injector.eager;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class BasicEagerDependencyModel_Sting_MyModel3 {
  private BasicEagerDependencyModel_Sting_MyModel3() {
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public static Object create(final Object modelA, final Object modelB) {
    return new BasicEagerDependencyModel.MyModel3( Objects.requireNonNull( (BasicEagerDependencyModel.MyModel0) modelA ), Objects.requireNonNull( (BasicEagerDependencyModel.MyModel2) modelB ) );
  }
}
