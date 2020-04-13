package com.example.injector.includes;

import sting.Injectable;
import sting.Injector;

public interface SelfIncludesModel
{
  @Injector( includes = { MyComponent.class, MyInjector.class } )
  interface MyInjector
  {
    MyComponent getMyComponent();
  }

  @Injectable
  class MyComponent
  {
  }
}
