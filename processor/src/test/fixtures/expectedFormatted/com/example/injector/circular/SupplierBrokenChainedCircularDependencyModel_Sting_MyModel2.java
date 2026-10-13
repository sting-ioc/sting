package com.example.injector.circular;

import java.util.Objects;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenChainedCircularDependencyModel_Sting_MyModel2 {
  private SupplierBrokenChainedCircularDependencyModel_Sting_MyModel2() {}

  @SuppressWarnings({"ImpossibleNullComparison", "UnnecessaryCheckNotNull", "unchecked"})
  public static Object create(final Object model) {
    return new SupplierBrokenChainedCircularDependencyModel.MyModel2(
        Objects.requireNonNull((SupplierBrokenChainedCircularDependencyModel.MyModel3) model));
  }
}
