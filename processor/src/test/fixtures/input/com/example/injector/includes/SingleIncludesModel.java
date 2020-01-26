package com.example.injector.includes;

import sting.Fragment;
import sting.Injector;

@Injector( includes = SingleIncludesModel.MyFragment.class )
interface SingleIncludesModel
{
  Runnable getRunnable();

  @Fragment
  interface MyFragment
  {
    default Runnable provideRunnable()
    {
      return null;
    }
  }
}
