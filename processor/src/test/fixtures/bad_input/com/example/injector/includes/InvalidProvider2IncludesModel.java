package com.example.injector.includes;

import sting.Injector;

@Injector( includes = InvalidProvider2IncludesModel.MyComponent.class )
public interface InvalidProvider2IncludesModel
{
  MyComponent getMyComponent();

  @interface StingProvider
  {
    int value();
  }

  @StingProvider( 42 )
  @interface MyFrameworkComponent1
  {
  }

  @MyFrameworkComponent1
  class MyComponent
  {
  }
}
