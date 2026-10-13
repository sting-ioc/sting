package com.example.injector.inputs;

import java.util.Objects;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class PrimitiveInputBoxedDependencyInjectorModel_Sting_MyModel {
  private PrimitiveInputBoxedDependencyInjectorModel_Sting_MyModel() {
  }

  public static Object create(final Integer value) {
    return new PrimitiveInputBoxedDependencyInjectorModel.MyModel( Objects.requireNonNull( value ) );
  }
}
