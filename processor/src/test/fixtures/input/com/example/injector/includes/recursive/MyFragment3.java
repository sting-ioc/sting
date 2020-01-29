package com.example.injector.includes.recursive;

import sting.Fragment;
import sting.Provides;

@Fragment
interface MyFragment3
{
  @Provides( qualifier = "Fragment3" )
  default Runnable provideRunnable()
  {
    return null;
  }
}
