package com.example.injector.includes;

import sting.Injector;
import sting.StingProvider;

@Injector( includes = UnannotatedProviderIncludesModel.MyComponent.class )
public interface UnannotatedProviderIncludesModel
{
  MyComponent getMyComponent();

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
