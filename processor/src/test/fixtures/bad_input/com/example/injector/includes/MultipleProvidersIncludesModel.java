package com.example.injector.includes;

import sting.Injector;
import sting.StingProvider;

@Injector( includes = MultipleProvidersIncludesModel.MyComponent.class )
public interface MultipleProvidersIncludesModel
{
  MyComponent getMyComponent();

  @StingProvider( "[FlatEnclosingName]MF1_[SimpleName]_Provider" )
  @interface MyFrameworkComponent1
  {
  }

  @StingProvider( "[FlatEnclosingName]MF2_[SimpleName]_Provider" )
  @interface MyFrameworkComponent2
  {
  }

  @MyFrameworkComponent1
  @MyFrameworkComponent2
  class MyComponent
  {
  }
}
