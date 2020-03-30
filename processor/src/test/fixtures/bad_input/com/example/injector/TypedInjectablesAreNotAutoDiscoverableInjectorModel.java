package com.example.injector;

import sting.Injectable;
import sting.Injector;
import sting.Typed;

public interface TypedInjectablesAreNotAutoDiscoverableInjectorModel
{
  @Injector
  interface MyInjector
  {
    MyModel getMyModel();
  }

  @Injectable
  @Typed( { Object.class, MyModel.class } )
  class MyModel
  {
  }
}
