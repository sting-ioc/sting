package com.example.injector;

import sting.Fragment;
import sting.Injector;

@Injector( includes = { MultipleCandidatesForSingularDependencyModel.MyFragment1.class,
                        MultipleCandidatesForSingularDependencyModel.MyFragment2.class } )
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
