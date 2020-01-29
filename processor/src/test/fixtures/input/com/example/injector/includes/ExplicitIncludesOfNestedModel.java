package com.example.injector.includes;

import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector( includes = { ExplicitIncludesOfNestedModel.MyFragment.class, ExplicitIncludesOfNestedModel.MyModel.class } )
interface ExplicitIncludesOfNestedModel
{
  Runnable getRunnable();

  MyModel getMyModel();

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
