package com.example.injector.eager;

import java.util.function.Supplier;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class BasicEagerDependencyModel_Sting_MyModel4 {
  private BasicEagerDependencyModel_Sting_MyModel4() {
  }

  @SuppressWarnings({
      "rawtypes",
      "unchecked"
  })
  public static Object create(final Object modelA, final Supplier modelB, final Supplier modelC) {
    return new BasicEagerDependencyModel.MyModel4( (BasicEagerDependencyModel.MyModel2) modelA, (Supplier<BasicEagerDependencyModel.MyModel3>) modelB, (Supplier<BasicEagerDependencyModel.MyModel1>) modelC );
  }
}
