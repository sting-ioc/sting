package com.example.injector;

import sting.Fragment;
import sting.Injector;

@Injector( includes = { MultipleCandidatesForSingularDependencyModel.MyFragment1.class,
                        MultipleCandidatesForSingularDependencyModel.MyFragment2.class } )
abstract class MultipleCandidatesForSingularDependencyModel
{
  abstract Runnable getRunnable();

  @Fragment
  public interface MyFragment1
  {
    default Runnable provideRunnable1()
    {
      return null;
    }
  }

  @Fragment
  public interface MyFragment2
  {
    default Runnable provideRunnable2()
    {
      return null;
    }
  }
}
