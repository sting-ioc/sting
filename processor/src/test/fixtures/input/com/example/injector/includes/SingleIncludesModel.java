package com.example.injector.includes;

import sting.Fragment;
import sting.Injector;

@Injector( includes = SingleIncludesModel.MyFragment.class )
abstract class SingleIncludesModel
{
  abstract Runnable getRunnable();

  @Fragment
  public interface MyFragment
  {
    default Runnable provideRunnable()
    {
      return null;
    }
  }
}
