package com.example.injector;

import sting.Injectable;
import sting.Injector;

@Injector
abstract class BasicInjectorModel
{
  abstract MyModel getMyModel();

  @Injectable
  static class MyModel
  {
  }
}
