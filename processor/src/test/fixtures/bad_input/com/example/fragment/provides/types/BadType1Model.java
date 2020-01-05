package com.example.fragment.provides.types;

import sting.Fragment;
import sting.Provides;

@Fragment
public interface BadType1Model
{
  class Foo
  {
  }

  @Provides( types = Runnable.class )
  default Foo provideX()
  {
    return null;
  }
}
