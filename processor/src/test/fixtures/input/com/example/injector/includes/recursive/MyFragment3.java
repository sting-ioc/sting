package com.example.injector.includes.recursive;

import sting.Fragment;
import sting.Named;

@Fragment
interface MyFragment3
{
  @Named( "Fragment3" )
  default Runnable provideRunnable()
  {
    return null;
  }
}
