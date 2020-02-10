package com.example.injector.outputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_CollectionContainingMultipleInstancesOutputModel_Provider {
  @Nonnull
  default CollectionContainingMultipleInstancesOutputModel provide() {
    return new Sting_CollectionContainingMultipleInstancesOutputModel();
  }
}
