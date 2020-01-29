package com.example.injector;

import sting.Fragment;
import sting.Injector;

@Injector
interface MultipleCandidatesForSingularDependencyModel
{
  Runnable getRunnable();

  @Fragment
  interface MyFragment1
  {
    default Runnable provideRunnable1()
    {
      return null;
    }
  }

  @Fragment
  interface MyFragment2
  {
    default Runnable provideRunnable2()
    {
      return null;
    }
  }
}
