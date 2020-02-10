package com.example.injector.outputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_OptionalMissingOutputModel_Provider {
  @Nonnull
  default OptionalMissingOutputModel provide() {
    return new Sting_OptionalMissingOutputModel();
  }

  @Nullable
  default OptionalMissingOutputModel.MyModel1 getMyModel1(
      final OptionalMissingOutputModel injector) {
    return injector.getMyModel1();
  }

  default OptionalMissingOutputModel.MyModel2 getMyModel2(
      final OptionalMissingOutputModel injector) {
    return injector.getMyModel2();
  }
}
