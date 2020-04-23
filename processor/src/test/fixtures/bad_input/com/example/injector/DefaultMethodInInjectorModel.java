package com.example.injector;

import sting.Injectable;
import sting.Injector;

@Injector
interface DefaultMethodInInjectorModel
{
  default void doStuff()
  {
  }

  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
