package com.example.injector.includes;

import sting.Injector;
import sting.StingProvider;

@Injector( includes = MissingProviderIncludesModel.MyComponent.class )
public interface MissingProviderIncludesModel
{
  MyComponent getMyComponent();

  @StingProvider( "[FlatEnclosingName]MF1_[SimpleName]_Provider" )
  @interface MyFrameworkComponent1
  {
  }

  @MyFrameworkComponent1
  class MyComponent
  {
  }
}
