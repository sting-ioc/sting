package com.example.fragment.includes;

import javax.annotation.Nonnull;
import sting.Fragment;

@Fragment( includes = InvalidProvider2IncludesModel.MyComponent.class )
public interface InvalidProvider2IncludesModel
{
  @interface StingProvider
  {
    int value();
  }

  @StingProvider( 23 )
  @interface MyFrameworkComponent1
  {
  }

  @MyFrameworkComponent1
  class MyComponent
  {
  }
}
