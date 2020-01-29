package com.example.deprecated;

import sting.Fragment;
import sting.Injector;

@Injector
public interface DeprecatedProvidesNodeInjectorModel
{
  MyModel getMyModel();

  class MyModel
  {
  }

  @Fragment
  interface MyFragment
  {
    @Deprecated
    default MyModel provideMyModel()
    {
      return null;
    }
  }
}
