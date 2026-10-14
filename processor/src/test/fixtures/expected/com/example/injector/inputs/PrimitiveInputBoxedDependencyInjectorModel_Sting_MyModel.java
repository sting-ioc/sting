package com.example.injector.inputs;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class PrimitiveInputBoxedDependencyInjectorModel_Sting_MyModel {
  private PrimitiveInputBoxedDependencyInjectorModel_Sting_MyModel() {
  }

  public static Object create(final Integer value) {
    return new PrimitiveInputBoxedDependencyInjectorModel.MyModel( value );
  }
}
