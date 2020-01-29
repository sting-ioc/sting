package com.example.deprecated;

import sting.Fragment;
import sting.Injector;

@Injector( includes = DeprecatedFragmentNodeInjectorModel.MyFragment.class )
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
