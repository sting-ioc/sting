package com.example.fragment.provides;

import sting.Fragment;

@Fragment
public interface ParameterizedProvidesModel
{
  default <T> String provideX()
  {
    return null;
  }
}
