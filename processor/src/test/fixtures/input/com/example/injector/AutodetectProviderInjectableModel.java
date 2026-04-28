package com.example.injector;

import sting.Injectable;
import sting.Injector;
import sting.StingProvider;
import sting.Typed;

interface AutodetectProviderInjectableModel
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

  @Injectable
  @Typed( MyModel1.class )
  class MyModel1Impl
    extends MyModel1
  {
  }
}
