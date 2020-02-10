package com.example.injector.includes;

import java.util.EventListener;
import sting.Injectable;
import sting.Injector;

@Injector( includes = InvalidProvider1IncludesModel.MyComponent.class )
public interface InvalidProvider1IncludesModel
{
  MyComponent getMyComponent();

  @interface StingProvider
  {
    String name();
  }

  @StingProvider( name = "[FlatEnclosingName]MF1_[SimpleName]_Provider" )
  @interface MyFrameworkComponent1
  {
  }

  @MyFrameworkComponent1
  class MyComponent
  {
  }
}
