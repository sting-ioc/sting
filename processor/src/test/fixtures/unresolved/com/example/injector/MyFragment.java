package com.example.injector;

import sting.Fragment;

@Fragment
public interface MyFragment
{
  default Runnable provideRunnable()
  {
    return null;
  }
}
