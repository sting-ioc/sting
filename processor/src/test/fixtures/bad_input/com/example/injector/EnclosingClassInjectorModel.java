package com.example.injector;

import sting.Injectable;
import sting.Injector;

@Injector
interface EnclosingClassInjectorModel
{
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }

  class Foo
  {
  }
}
