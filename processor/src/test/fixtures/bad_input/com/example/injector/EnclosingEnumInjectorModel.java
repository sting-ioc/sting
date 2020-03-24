package com.example.injector;

import sting.Injectable;
import sting.Injector;

@Injector
interface EnclosingEnumInjectorModel
{
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }

  enum Foo
  {
  }
}
