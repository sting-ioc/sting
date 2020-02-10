package com.example.injector.outputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_OptionalProvidesOutputModel_Provider {
  @Nonnull
  default OptionalProvidesOutputModel provide() {
    return new Sting_OptionalProvidesOutputModel();
  }
}
