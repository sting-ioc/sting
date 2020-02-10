package com.example.injector.outputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_OptionalOutputModel_Provider {
  @Nonnull
  default OptionalOutputModel provide() {
    return new Sting_OptionalOutputModel();
  }

  @Nullable
  default OptionalOutputModel.MyModel getMyModel(final OptionalOutputModel injector) {
    return injector.getMyModel();
  }
}
