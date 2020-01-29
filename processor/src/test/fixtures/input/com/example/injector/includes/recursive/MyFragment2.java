package com.example.injector.includes.recursive;

import sting.Fragment;
import sting.Provides;

@Fragment( includes = { MyFragment3.class, MyModel3.class } )
interface MyFragment2
{
  @Provides( qualifier = "Fragment2" )
  default Runnable provideRunnable()
  {
    return null;
  }
}
