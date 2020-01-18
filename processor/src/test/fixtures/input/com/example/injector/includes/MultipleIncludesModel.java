package com.example.injector.includes;

import sting.Factory;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector( includes = { MultipleIncludesModel.MyFragment.class,
                        MultipleIncludesModel.MyFactory.class,
                        MultipleIncludesModel.MyModel.class } )
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

  @Factory
  public interface MyFactory
  {
    Runnable create();
  }

  @Injectable
  static class MyModel
  {
  }
}
