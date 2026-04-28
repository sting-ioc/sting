package com.example.injector.includes;

import sting.Injectable;
import sting.Injector;

public interface DuplicateIncludesModel
{
  @Injector( fragmentOnly = false, includes = { MyComponent.class, MyComponent.class } )
  interface MyInjector
  {
    MyComponent getMyComponent();
  }

  @Injectable
  class MyComponent
  {
  }
}
