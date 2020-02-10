package com.example.fragment.includes;

import sting.Fragment;

@Fragment( includes = InvalidProvider1IncludesModel.MyComponent.class )
public interface InvalidProvider1IncludesModel
{
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
