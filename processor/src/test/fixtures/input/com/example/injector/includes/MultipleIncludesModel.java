package com.example.injector.includes;

import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector( includes = { MultipleIncludesModel.MyFragment.class, MultipleIncludesModel.MyModel.class } )
abstract class MultipleIncludesModel
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

  @Injectable
  static class MyModel
  {
  }
}
