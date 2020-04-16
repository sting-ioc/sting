package com.example.injector.includes;

import sting.Injectable;
import sting.Injector;
import sting.StingProvider;
import sting.Typed;

public interface UnusedStingProviderIncludesModel
{
  @Injector( includes = MyModel1.class )
  interface MyInjector
  {
    MyModel2 getMyModel2();
  }

  @StingProvider( "[CompoundName]Impl" )
  @interface MyFramework
  {
  }

  @MyFramework
  class MyModel1
  {
  }

  @Injectable
  @Typed( MyModel1.class )
  class MyModel1Impl
    extends MyModel1
  {
  }

  @Injectable
  class MyModel2
  {
  }

}
