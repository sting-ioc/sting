package com.example.injector;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class BasicInjectorModel_Sting_MyModel {
  private BasicInjectorModel_Sting_MyModel() {
  }

  @Nonnull
  public static Object create() {
    return new BasicInjectorModel.MyModel();
  }
}
