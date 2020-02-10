package com.example.injector.includes.single;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_SingleIncludesModel_Provider {
  @Nonnull
  default SingleIncludesModel provide() {
    return new Sting_SingleIncludesModel();
  }
}
