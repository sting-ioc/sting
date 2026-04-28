package com.example.factory;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_ParameterAnnotationsFactoryModel_Sting_MyComponentFactory {
  private Sting_ParameterAnnotationsFactoryModel_Sting_MyComponentFactory() {
  }

  @Nonnull
  public static ParameterAnnotationsFactoryModel_Sting_MyComponentFactory create(
      @Nonnull final ParameterAnnotationsFactoryModel.SomeService someService) {
    return new ParameterAnnotationsFactoryModel_Sting_MyComponentFactory( Objects.requireNonNull( someService ) );
  }
}
