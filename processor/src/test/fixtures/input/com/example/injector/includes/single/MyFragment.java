package com.example.injector.includes.single;

import sting.Fragment;

@Fragment
interface MyFragment
{
  default Runnable provideRunnable()
  {
    return null;
  }
}
