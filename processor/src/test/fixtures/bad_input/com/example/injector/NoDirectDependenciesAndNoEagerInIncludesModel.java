package com.example.injector;

import sting.Fragment;
import sting.Injector;

@Injector( includes = NoDirectDependenciesAndNoEagerInIncludesModel.MyFragment.class )
interface NoDirectDependenciesAndNoEagerInIncludesModel
{
  @Fragment
  interface MyFragment
  {
    default Runnable provideRunnable()
    {
      return null;
    }
  }
}
