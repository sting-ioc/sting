package com.example.fragment.provides;

import sting.Fragment;
import sting.Provides;

@Fragment
public interface IncompatibleReturnTypeProvidesModel
{
  @Provides( types = Boolean.class )
  default String provideX()
  {
    return null;
  }
}
