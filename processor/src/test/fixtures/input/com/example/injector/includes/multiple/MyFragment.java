package com.example.injector.includes.multiple;

import sting.Fragment;

@Fragment
public interface MyFragment
{
  default Runnable provideRunnable()
  {
    return null;
  }
}
