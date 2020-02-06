package com.example.fragment.types;

import sting.Eager;
import sting.Fragment;
import sting.Provides;

@Fragment
public interface NoTypesModel
{
  class MyModel
  {
  }

  @Eager
  @Provides( services = {} )
  default MyModel provideMyModel()
  {
    return null;
  }
}
