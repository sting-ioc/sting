package com.example.injector.inputs;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class MultipleInputInjectorModel_Sting_MyModel {
  private MultipleInputInjectorModel_Sting_MyModel() {
  }

  public static Object create(final Runnable runnable) {
    return new MultipleInputInjectorModel.MyModel( runnable );
  }
}
