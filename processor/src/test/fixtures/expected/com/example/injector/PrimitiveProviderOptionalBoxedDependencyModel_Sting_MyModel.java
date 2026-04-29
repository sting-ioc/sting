package com.example.injector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class PrimitiveProviderOptionalBoxedDependencyModel_Sting_MyModel {
  private PrimitiveProviderOptionalBoxedDependencyModel_Sting_MyModel() {
  }

  @Nonnull
  public static Object create(@Nullable final Integer value) {
    return new PrimitiveProviderOptionalBoxedDependencyModel.MyModel( value );
  }
}
