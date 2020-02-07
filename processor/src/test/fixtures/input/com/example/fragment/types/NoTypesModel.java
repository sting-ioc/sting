package com.example.fragment.types;

import sting.Eager;
import sting.Fragment;
import sting.Provides;
import sting.Typed;

@Fragment
public interface NoTypesModel
{
  class MyModel
  {
  }

  @Eager
  @Typed( {} )
  default MyModel provideMyModel()
  {
    return null;
  }
}
