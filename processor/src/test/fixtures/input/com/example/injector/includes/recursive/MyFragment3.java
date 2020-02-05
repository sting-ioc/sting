package com.example.injector.includes.recursive;

import sting.Fragment;
import sting.Provides;
import sting.Service;

@Fragment
interface MyFragment3
{
  @Provides( services = @Service( qualifier = "Fragment3" ) )
  default Runnable provideRunnable()
  {
    return null;
  }
}
