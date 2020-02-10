package com.example.injector.outputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_OptionalMissingOutputModel_Provider {
  @Nonnull
  default OptionalMissingOutputModel provide() {
    return new Sting_OptionalMissingOutputModel();
  }
}
