package com.example.injector.includes;

import sting.Injector;
import sting.StingProvider;

public interface UnannotatedProviderIncludesModel
{
  @Injector( includes = MyComponent.class )
  interface MyInjector
  {
    MyComponent getMyComponent();
  }

  @StingProvider( "[EnclosingName][SimpleName]_Provider" )
  @interface MyFrameworkComponent1
  {
  }

  @MyFrameworkComponent1
  class MyComponent
  {
  }

  class MyComponent_Provider
  {
  }
}
