package com.example.fragment.includes;

import sting.Fragment;
import sting.StingProvider;

@Fragment( includes = MissingProviderIncludesModel.MyComponent.class )
public interface MissingProviderIncludesModel
{
  @StingProvider( "[FlatEnclosingName]MF1_[SimpleName]_Provider" )
  @interface MyFrameworkComponent1
  {
  }

  @MyFrameworkComponent1
  class MyComponent
  {
  }
}
