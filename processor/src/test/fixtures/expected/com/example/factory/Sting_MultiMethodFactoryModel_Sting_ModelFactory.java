package com.example.factory;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_MultiMethodFactoryModel_Sting_ModelFactory {
  private Sting_MultiMethodFactoryModel_Sting_ModelFactory() {
  }

  public static MultiMethodFactoryModel_Sting_ModelFactory create(
      final MultiMethodFactoryModel.SomeService someService,
      final MultiMethodFactoryModel.OtherService otherService) {
    return new MultiMethodFactoryModel_Sting_ModelFactory( someService, otherService );
  }
}
