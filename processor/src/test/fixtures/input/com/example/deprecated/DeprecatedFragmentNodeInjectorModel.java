package com.example.deprecated;

import sting.Fragment;
import sting.Injector;

@Injector
public interface DeprecatedFragmentNodeInjectorModel
{
  Runnable getRunnable();

  @Deprecated
  @Fragment
  interface MyFragment
  {
    default Runnable provideRunnable()
    {
      return null;
    }
  }
}
