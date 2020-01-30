package com.example.injector.dependency;

import sting.Dependency;
import sting.Injectable;
import sting.Injector;

@Injector
interface BadTypeParameterDependencyModel
{
  @Dependency( type = Runnable.class )
  MyModel1 getMyModel1();

  @Injectable
  class MyModel1
  {
  }
}