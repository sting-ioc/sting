package com.example.factory;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_MultiMethodFactoryModel_Sting_ModelFactory {
  private Sting_MultiMethodFactoryModel_Sting_ModelFactory() {
  }

  @Nonnull
  public static MultiMethodFactoryModel_Sting_ModelFactory create(
      @Nonnull final MultiMethodFactoryModel.SomeService someService,
      @Nonnull final MultiMethodFactoryModel.OtherService otherService) {
    return new MultiMethodFactoryModel_Sting_ModelFactory( Objects.requireNonNull( someService ), Objects.requireNonNull( otherService ) );
  }
}
