package com.example.injector.dependency;

import sting.Injectable;
import sting.Injector;

@Injector
interface BasicDependencyModel
{
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
