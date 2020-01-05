package com.example.fragment.provides;

import sting.Fragment;

@Fragment
public interface BadReturnTypeProvidesModel
{
  default void provideX()
  {
  }
}
