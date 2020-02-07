package com.example.fragment.provides.types;

import sting.Fragment;
import sting.Typed;

@Fragment
public interface NoTypesAndLazyModel
{
  @Typed( {} )
  default String provideX()
  {
    return "";
  }
}
