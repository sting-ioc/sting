package com.example.injector;

import sting.Injectable;
import sting.Injector;
import sting.Named;

public interface NamedInjectablesAreNotAutoDiscoverableInjectorModel
{
  @Injector
  interface MyInjector
  {
    MyModel getMyModel();
  }

  @Injectable
  @Named( "MyQualifier" )
  class MyModel
  {
  }
}
