package com.example.injector;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class PrimitiveProviderOptionalBoxedDependencyModel_Sting_MyModel {
  private PrimitiveProviderOptionalBoxedDependencyModel_Sting_MyModel() {}

  @SuppressWarnings({"ImpossibleNullComparison", "UnnecessaryCheckNotNull"})
  public static Object create(final @Nullable Integer value) {
    return new PrimitiveProviderOptionalBoxedDependencyModel.MyModel(value);
  }
}
