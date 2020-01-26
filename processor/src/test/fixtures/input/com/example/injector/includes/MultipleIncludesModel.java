package com.example.injector.includes;

import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector( includes = { MultipleIncludesModel.MyFragment.class, MultipleIncludesModel.MyModel.class } )
interface MultipleIncludesModel
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

  @Injectable
  class MyModel
  {
  }
}
