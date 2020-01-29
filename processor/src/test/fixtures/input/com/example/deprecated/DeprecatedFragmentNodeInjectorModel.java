package com.example.deprecated;

import sting.Fragment;
import sting.Injector;

@Injector
public interface DeprecatedFragmentNodeInjectorModel
{
  MyModel getMyModel();

  class MyModel
  {
  }

  @Deprecated
  @Fragment
  interface MyFragment
  {
    default MyModel provideMyModel()
    {
      return null;
    }
  }
}
