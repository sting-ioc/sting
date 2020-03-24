package com.example.fragment.includes;

import sting.Fragment;
import sting.StingProvider;

public interface UnannotatedProviderIncludesModel
{
  @Fragment( includes = MyComponent.class )
  interface MyInjector
  {
  }

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
