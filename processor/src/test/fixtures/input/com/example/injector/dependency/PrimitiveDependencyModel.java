package com.example.injector.dependency;

import sting.Fragment;
import sting.Injector;

@Injector( includes = PrimitiveDependencyModel.MyFragment.class )
abstract class PrimitiveDependencyModel
{
  abstract int getMyModel();

  @Fragment
  public interface MyFragment
  {
    default int provideValue()
    {
      return 0;
    }
  }
}
