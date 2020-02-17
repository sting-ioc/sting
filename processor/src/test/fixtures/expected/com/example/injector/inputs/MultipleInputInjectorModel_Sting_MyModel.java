package com.example.injector.inputs;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class MultipleInputInjectorModel_Sting_MyModel {
  private MultipleInputInjectorModel_Sting_MyModel() {
  }

  @Nonnull
  public static Object create(final Runnable runnable) {
    return new MultipleInputInjectorModel.MyModel( Objects.requireNonNull( runnable ) );
  }
}
