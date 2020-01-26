package com.example.injector.circular;

import sting.Injectable;
import sting.Injector;

@Injector
interface DirectlyCircularDependencyModel
{
  MyModel1 getMyModel1();

  MyModel2 getMyModel2();

  @Injectable
  class MyModel1
  {
    MyModel1( MyModel2 model )
    {
    }
  }

  @Injectable
  class MyModel2
  {
    MyModel2( MyModel1 model )
    {
    }
  }
}
