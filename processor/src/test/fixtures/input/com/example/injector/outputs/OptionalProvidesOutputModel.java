package com.example.injector.outputs;

import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injector;

@Injector
interface OptionalProvidesOutputModel
{
  @Nullable
  Runnable getRunnable();

  @Fragment
  interface MyFragment
  {
    @Nullable
    default Runnable provideRunnable()
    {
      return null;
    }
  }
}
