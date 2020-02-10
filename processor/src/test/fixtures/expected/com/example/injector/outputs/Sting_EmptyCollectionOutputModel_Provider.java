package com.example.injector.outputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_EmptyCollectionOutputModel_Provider {
  @Nonnull
  default EmptyCollectionOutputModel provide() {
    return new Sting_EmptyCollectionOutputModel();
  }
}
