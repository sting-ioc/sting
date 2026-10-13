package com.example.factory;

import java.util.Objects;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_ParameterAnnotationsFactoryModel_Sting_MyComponentFactory {
  private Sting_ParameterAnnotationsFactoryModel_Sting_MyComponentFactory() {
  }

  @SuppressWarnings({
      "ImpossibleNullComparison",
      "UnnecessaryCheckNotNull"
  })
  public static ParameterAnnotationsFactoryModel_Sting_MyComponentFactory create(
      final ParameterAnnotationsFactoryModel.SomeService someService) {
    return new ParameterAnnotationsFactoryModel_Sting_MyComponentFactory( Objects.requireNonNull( someService ) );
  }
}
