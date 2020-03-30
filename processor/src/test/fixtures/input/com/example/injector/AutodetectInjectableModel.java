package com.example.injector;

import sting.Injectable;
import sting.Injector;

interface AutodetectInjectableModel
{
  @Injector
  interface MyInjector
  {
    MyModel1 getMyModel1();
  }

  @Injectable
  class MyModel1
  {
  }
}
