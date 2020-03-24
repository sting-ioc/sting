package com.example.fragment.provides.types;

import sting.Fragment;
import sting.Typed;

public interface BadType1Model
{
  class Foo
  {
  }

  @Fragment
  interface MyFragment
  {
    @Typed( Runnable.class )
    default Foo provideX()
    {
      return null;
    }
  }
}
