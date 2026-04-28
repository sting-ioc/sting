package com.example.injector;

import sting.Fragment;
import sting.Injector;
import sting.StingProvider;

interface AutodetectProviderFragmentModel
{
  @Injector
  interface MyInjector
  {
    MyModel1 getMyModel1();
  }

  @StingProvider( "[CompoundName]Fragment" )
  @interface MyFrameworkComponent
  {
  }

  @MyFrameworkComponent
  class MyModel1
  {
  }

  @Fragment
  interface MyModel1Fragment
  {
    default MyModel1 provideMyModel1()
    {
      return new MyModel1();
    }
  }
}
