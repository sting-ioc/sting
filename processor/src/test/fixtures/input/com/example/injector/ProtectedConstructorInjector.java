package com.example.injector;

import sting.Injectable;
import sting.Injector;

@Injector
abstract class ProtectedConstructorInjector
{
  protected ProtectedConstructorInjector()
  {
  }

  abstract MyModel getMyModel();

  @Injectable
  static class MyModel
  {
  }
}
