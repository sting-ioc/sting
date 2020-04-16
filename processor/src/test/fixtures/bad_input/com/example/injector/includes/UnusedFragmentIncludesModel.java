package com.example.injector.includes;

import sting.Fragment;
import sting.Injectable;
import sting.Injector;

public interface UnusedFragmentIncludesModel
{
  @Injector( includes = MyFragment.class )
  interface MyInjector
  {
    MyModel2 getMyModel2();
  }

  @Fragment
  interface MyFragment
  {
    default MyModel1 provideMyModel1()
    {
      return null;
    }
  }

  class MyModel1
  {
  }

  @Injectable
  class MyModel2
  {
  }
}
