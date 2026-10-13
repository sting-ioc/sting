package com.example.injector.circular;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class SupplierBrokenDirectCircularDependencyModel_Sting_MyModel2 {
  private SupplierBrokenDirectCircularDependencyModel_Sting_MyModel2() {}

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static Object create(final Supplier model) {
    return new SupplierBrokenDirectCircularDependencyModel.MyModel2(Objects.requireNonNull(
        (Supplier<SupplierBrokenDirectCircularDependencyModel.MyModel1>) model));
  }
}
