package com.example.injector.eager;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class BasicEagerDependencyModel_Sting_MyModel6 {
  private BasicEagerDependencyModel_Sting_MyModel6() {
  }

  @SuppressWarnings("unchecked")
  public static Object create(final Object modelA, final Object modelB) {
    return new BasicEagerDependencyModel.MyModel6( (BasicEagerDependencyModel.MyModel4) modelA, (BasicEagerDependencyModel.MyModel5) modelB );
  }
}
