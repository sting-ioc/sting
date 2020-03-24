package com.example.injector.outputs;

import java.util.Collection;
import sting.Fragment;
import sting.Injector;

@Injector
interface CollectionContainingMultipleInstancesOutputModel
{
  Collection<Runnable> getRunnables();

  @Fragment
  interface MyFragment1
  {
    default Runnable provideRunnable()
    {
      return null;
    }
  }

  @Fragment
  interface MyFragment2
  {
    default Runnable provideRunnable()
    {
      return null;
    }
  }

  @Fragment
  interface MyFragment3
  {
    default Runnable provideRunnable()
    {
      return null;
    }
  }
}
