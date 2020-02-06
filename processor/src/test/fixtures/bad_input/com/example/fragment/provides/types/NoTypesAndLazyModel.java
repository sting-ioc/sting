package com.example.fragment.provides.types;

import sting.Fragment;
import sting.Provides;

@Fragment
public interface NoTypesAndLazyModel
{
  @Provides( services = {} )
  default String provideX()
  {
    return "";
  }
}
