package com.example.injector.eager;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class BasicEagerDependencyModel_Sting_MyModel5 {
  private BasicEagerDependencyModel_Sting_MyModel5() {
  }

  @Nonnull
  @SuppressWarnings({
      "rawtypes",
      "unchecked"
  })
  public static Object create(final Object modelA, final Supplier modelB, final Object modelC) {
    return new BasicEagerDependencyModel.MyModel5( Objects.requireNonNull( (BasicEagerDependencyModel.MyModel2) modelA ), Objects.requireNonNull( (Supplier<BasicEagerDependencyModel.MyModel3>) modelB ), Objects.requireNonNull( (BasicEagerDependencyModel.MyModel1) modelC ) );
  }
}
