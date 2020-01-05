package com.example.fragment.types;

import sting.Fragment;
import sting.Provides;

@Fragment
public interface NoTypesModel
{
  class MyModel
  {
  }

  @Provides( types = {}, eager = true )
  default MyModel provideMyModel()
  {
    return null;
  }
}
