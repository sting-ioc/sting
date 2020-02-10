package com.example.fragment.includes;

import sting.Fragment;
import sting.StingProvider;

@Fragment( includes = UnannotatedProviderIncludesModel.MyComponent.class )
public interface UnannotatedProviderIncludesModel
{
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
