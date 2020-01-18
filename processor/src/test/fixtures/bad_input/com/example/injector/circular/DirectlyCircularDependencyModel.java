package com.example.injector.circular;

import sting.Injectable;
import sting.Injector;

@Injector
abstract class DirectlyCircularDependencyModel
{
  abstract MyModel1 getMyModel1();

  abstract MyModel2 getMyModel2();

  @Injectable
  static class MyModel1
  {
    MyModel1( MyModel2 model )
    {
    }
  }

  @Injectable
  static class MyModel2
  {
    MyModel2( MyModel1 model )
    {
    }
  }
}
