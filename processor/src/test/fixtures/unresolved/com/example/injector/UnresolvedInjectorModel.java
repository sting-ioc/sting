package com.example.injector;

import sting.Injectable;
import sting.Injector;

@Injector
@org.jspecify.annotations.NullMarked
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
