package com.example.injector.includes.recursive;

import sting.Fragment;

@Fragment( includes = { MyFragment2.class, MyModel2.class } )
public interface MyFragment1
{
  default Runnable provideRunnable()
  {
    return null;
  }
}
