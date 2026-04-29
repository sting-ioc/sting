package com.example.injector.outputs;

import java.util.Collection;
import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injector;

@Injector
interface OptionalFilteredCollectionOutputModel
{
  Collection<Runnable> getRunnables();

  @Fragment
  interface MyFragment1
  {
    default Runnable provideRunnable1()
    {
      return () -> { };
    }
  }

  @Fragment
  interface MyFragment2
  {
    @Nullable
    default Runnable provideRunnable2()
    {
      return null;
    }
  }
}
