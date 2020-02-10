package com.example.injector.eager;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_EagerInjectableViaIncludesModel_Provider {
  @Nonnull
  default EagerInjectableViaIncludesModel provide() {
    return new Sting_EagerInjectableViaIncludesModel();
  }
}
