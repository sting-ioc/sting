package com.example.fragment.includes;

import sting.Fragment;
import sting.Injectable;

public interface DuplicateIncludesModel
{
  @Fragment( includes = { MyComponent.class, MyComponent.class } )
  interface MyInjector
  {
  }

  @Injectable
  class MyComponent
  {
  }
}
