package com.example.injector.inputs;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class PrimitiveInputBoxedDependencyInjectorModel_Sting_MyModel {
  private PrimitiveInputBoxedDependencyInjectorModel_Sting_MyModel() {
  }

  @Nonnull
  public static Object create(final Integer value) {
    return new PrimitiveInputBoxedDependencyInjectorModel.MyModel( Objects.requireNonNull( value ) );
  }
}
