package com.example.injector.includes.recursive;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_RecursiveIncludesModel_Provider {
  @Nonnull
  default RecursiveIncludesModel provide() {
    return new Sting_RecursiveIncludesModel();
  }
}
