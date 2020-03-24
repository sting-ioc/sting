package com.example.deprecated;

import sting.Fragment;
import sting.Injector;

@Injector
public interface DeprecatedProvidesNodeInjectorModel
{
  Runnable getRunnable();

  @Fragment
  interface MyFragment
  {
    @Deprecated
    default Runnable provideRunnable()
    {
      return null;
    }
  }
}
