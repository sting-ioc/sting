package com.example.injector.outputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_PrimitiveOutputModel_Provider {
  @Nonnull
  default PrimitiveOutputModel provide() {
    return new Sting_PrimitiveOutputModel();
  }

  default boolean getValue1(final PrimitiveOutputModel injector) {
    return injector.getValue1();
  }

  default char getValue2(final PrimitiveOutputModel injector) {
    return injector.getValue2();
  }

  default byte getValue3(final PrimitiveOutputModel injector) {
    return injector.getValue3();
  }

  default short getValue4(final PrimitiveOutputModel injector) {
    return injector.getValue4();
  }

  default int getValue5(final PrimitiveOutputModel injector) {
    return injector.getValue5();
  }

  default long getValue6(final PrimitiveOutputModel injector) {
    return injector.getValue6();
  }

  default float getValue7(final PrimitiveOutputModel injector) {
    return injector.getValue7();
  }

  default double getValue8(final PrimitiveOutputModel injector) {
    return injector.getValue8();
  }
}
