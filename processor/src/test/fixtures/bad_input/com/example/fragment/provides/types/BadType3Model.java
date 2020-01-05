package com.example.fragment.provides.types;

import sting.Fragment;
import sting.Provides;

@Fragment
public interface BadType3Model
{
  interface MyBaseInterface
  {
  }

  interface MyOuterInterface
    extends MyBaseInterface
  {
  }

  // Does not implement MyOuterInterface!
  @Provides( types = { MyBaseInterface.class, MyOuterInterface.class } )
  default MyBaseInterface provideX()
  {
    return null;
  }
}
