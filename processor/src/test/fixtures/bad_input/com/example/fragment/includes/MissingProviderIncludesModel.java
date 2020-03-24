package com.example.fragment.includes;

import sting.Fragment;
import sting.StingProvider;

public interface MissingProviderIncludesModel
{
  @Fragment( includes = MyComponent.class )
  interface MyInjector
  {
  }

  @StingProvider( "[FlatEnclosingName]MF1_[SimpleName]_Provider" )
  @interface MyFrameworkComponent1
  {
  }

  @MyFrameworkComponent1
  class MyComponent
  {
  }
}
