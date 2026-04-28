package com.example.injector.autodetect.provider;

import sting.Injector;
import sting.StingProvider;

public interface MissingProviderModel
{
  @Injector
  interface MyInjector
  {
    MyModel1 getMyModel1();
  }

  @StingProvider( "[CompoundName]Impl" )
  @interface MyFrameworkComponent
  {
  }

  @MyFrameworkComponent
  class MyModel1
  {
  }
}
