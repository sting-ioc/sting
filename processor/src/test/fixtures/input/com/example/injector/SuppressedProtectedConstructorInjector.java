package com.example.injector;

import sting.Injectable;
import sting.Injector;

@Injector
abstract class SuppressedProtectedConstructorInjector
{
  @SuppressWarnings( "Sting:ProtectedConstructor" )
  protected SuppressedProtectedConstructorInjector()
  {
  }

  abstract MyModel getMyModel();

  @Injectable
  static class MyModel
  {
  }
}
