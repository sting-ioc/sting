package com.example.fragment.provides.types;

import sting.Fragment;
import sting.Typed;

@Fragment
public interface BadType1Model
{
  class Foo
  {
  }

  @Typed( Runnable.class )
  default Foo provideX()
  {
    return null;
  }
}
