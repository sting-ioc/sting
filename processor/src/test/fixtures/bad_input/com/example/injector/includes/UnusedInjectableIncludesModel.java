package com.example.injector.includes;

import sting.Injectable;
import sting.Injector;

public interface UnusedInjectableIncludesModel
{
  @Injector( includes = MyModel1.class )
  interface MyInjector
  {
    MyModel2 getMyModel2();
  }

  @Injectable
  class MyModel1
  {
  }

  @Injectable
  class MyModel2
  {
  }
}
