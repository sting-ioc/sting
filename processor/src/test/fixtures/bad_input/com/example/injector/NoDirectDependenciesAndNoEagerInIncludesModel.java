package com.example.injector;

import sting.Fragment;
import sting.Injector;

@Injector( includes = NoDirectDependenciesAndNoEagerInIncludesModel.MyFragment.class )
abstract class NoDirectDependenciesAndNoEagerInIncludesModel
{
  @Fragment
  public interface MyFragment
  {
    default Runnable provideRunnable()
    {
      return null;
    }
  }
}
