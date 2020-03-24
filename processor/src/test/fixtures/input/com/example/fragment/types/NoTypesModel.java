package com.example.fragment.types;

import sting.Eager;
import sting.Fragment;
import sting.Typed;

@Fragment
public interface NoTypesModel
{
  @Eager
  @Typed( {} )
  default String provideConfig()
  {
    return null;
  }
}
