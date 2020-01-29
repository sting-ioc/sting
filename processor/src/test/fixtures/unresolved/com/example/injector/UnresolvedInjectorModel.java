package com.example.injector;

import sting.Injectable;
import sting.Injector;

@Injector
interface UnresolvedInjectorModel
{
  public static Sting_UnresolvedInjectorModel create()
  {
    return new Sting_UnresolvedInjectorModel();
  }

  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
