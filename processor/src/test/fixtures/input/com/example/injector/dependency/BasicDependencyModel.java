package com.example.injector.dependency;

import sting.Injectable;
import sting.Injector;

@Injector
abstract class BasicDependencyModel
{
  abstract MyModel getMyModel();

  @Injectable
  static class MyModel
  {
  }
}
