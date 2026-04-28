package com.example.factory;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_BasicFactoryModel_Sting_MyComponentFactory {
  private Sting_BasicFactoryModel_Sting_MyComponentFactory() {
  }

  @Nonnull
  public static BasicFactoryModel_Sting_MyComponentFactory create(
      @Nonnull final BasicFactoryModel.SomeService someService) {
    return new BasicFactoryModel_Sting_MyComponentFactory( Objects.requireNonNull( someService ) );
  }
}
