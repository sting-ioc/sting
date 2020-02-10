package com.example.injector.outputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_CollectionOutputModel_Provider {
  @Nonnull
  default CollectionOutputModel provide() {
    return new Sting_CollectionOutputModel();
  }
}
