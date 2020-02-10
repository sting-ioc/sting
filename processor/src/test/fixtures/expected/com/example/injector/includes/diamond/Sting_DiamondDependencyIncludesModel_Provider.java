package com.example.injector.includes.diamond;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_DiamondDependencyIncludesModel_Provider {
  @Nonnull
  default DiamondDependencyIncludesModel provide() {
    return new Sting_DiamondDependencyIncludesModel();
  }
}
