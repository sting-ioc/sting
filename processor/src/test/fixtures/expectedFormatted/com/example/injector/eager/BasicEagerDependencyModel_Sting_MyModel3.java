package com.example.injector.eager;

import java.util.Objects;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class BasicEagerDependencyModel_Sting_MyModel3 {
  private BasicEagerDependencyModel_Sting_MyModel3() {}

  @SuppressWarnings({"ImpossibleNullComparison", "UnnecessaryCheckNotNull", "unchecked"})
  public static Object create(final Object modelA, final Object modelB) {
    return new BasicEagerDependencyModel.MyModel3(
        Objects.requireNonNull((BasicEagerDependencyModel.MyModel0) modelA),
        Objects.requireNonNull((BasicEagerDependencyModel.MyModel2) modelB));
  }
}
