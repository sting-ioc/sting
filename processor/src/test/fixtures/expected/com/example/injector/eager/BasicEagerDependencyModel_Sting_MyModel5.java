package com.example.injector.eager;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class BasicEagerDependencyModel_Sting_MyModel5 {
  private BasicEagerDependencyModel_Sting_MyModel5() {
  }

  @SuppressWarnings({
      "ImpossibleNullComparison",
      "UnnecessaryCheckNotNull",
      "rawtypes",
      "unchecked"
  })
  public static Object create(final Object modelA, final Supplier modelB, final Object modelC) {
    return new BasicEagerDependencyModel.MyModel5( Objects.requireNonNull( (BasicEagerDependencyModel.MyModel2) modelA ), Objects.requireNonNull( (Supplier<BasicEagerDependencyModel.MyModel3>) modelB ), Objects.requireNonNull( (BasicEagerDependencyModel.MyModel1) modelC ) );
  }
}
