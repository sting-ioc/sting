package com.example.injector;

import sting.Fragment;
import sting.Injector;

@Injector
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
