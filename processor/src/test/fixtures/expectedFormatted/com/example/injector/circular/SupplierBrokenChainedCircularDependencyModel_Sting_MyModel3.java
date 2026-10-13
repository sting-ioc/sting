package com.example.injector.circular;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenChainedCircularDependencyModel_Sting_MyModel3 {
  private SupplierBrokenChainedCircularDependencyModel_Sting_MyModel3() {}

  @SuppressWarnings({"ImpossibleNullComparison", "UnnecessaryCheckNotNull", "rawtypes", "unchecked"
  })
  public static Object create(final Supplier model) {
    return new SupplierBrokenChainedCircularDependencyModel.MyModel3(Objects.requireNonNull(
        (Supplier<SupplierBrokenChainedCircularDependencyModel.MyModel1>) model));
  }
}
