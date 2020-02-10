package com.example.fragment.includes;

import sting.Fragment;
import sting.StingProvider;

@Fragment( includes = MultipleProvidersIncludesModel.MyComponent.class )
public interface MultipleProvidersIncludesModel
{
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
