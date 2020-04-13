package com.example.fragment.includes;

import sting.Fragment;
import sting.Injectable;

public interface SelfIncludesModel
{
  @Fragment( includes = { MyComponent.class, MyInjector.class } )
  interface MyInjector
  {
  }

  @Injectable
  class MyComponent
  {
  }
}
