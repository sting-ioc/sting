package com.example.injector.autodetect.provider;

import sting.Injector;
import sting.StingProvider;

public interface MultipleProvidersModel
{
  @Injector
  interface MyInjector
  {
    MyModel1 getMyModel1();
  }

  @StingProvider( "[CompoundName]Impl" )
  @interface MyFrameworkComponent1
  {
  }

  @StingProvider( "[CompoundName]OtherImpl" )
  @interface MyFrameworkComponent2
  {
  }

  @MyFrameworkComponent1
  @MyFrameworkComponent2
  class MyModel1
  {
  }
}
