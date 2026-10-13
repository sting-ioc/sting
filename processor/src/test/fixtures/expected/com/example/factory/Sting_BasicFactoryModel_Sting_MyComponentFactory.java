package com.example.factory;

import java.util.Objects;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_BasicFactoryModel_Sting_MyComponentFactory {
  private Sting_BasicFactoryModel_Sting_MyComponentFactory() {
  }

  @SuppressWarnings({
      "ImpossibleNullComparison",
      "UnnecessaryCheckNotNull"
  })
  public static BasicFactoryModel_Sting_MyComponentFactory create(
      final BasicFactoryModel.SomeService someService) {
    return new BasicFactoryModel_Sting_MyComponentFactory( Objects.requireNonNull( someService ) );
  }
}
