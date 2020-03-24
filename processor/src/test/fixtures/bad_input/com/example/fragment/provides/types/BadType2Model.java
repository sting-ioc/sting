package com.example.fragment.provides.types;

import java.util.concurrent.Callable;
import sting.Fragment;
import sting.Typed;

public interface BadType2Model
{
  class Foo
    implements Runnable
  {
    @Override
    public void run()
    {
    }
  }

  @Fragment
  interface MyFragment
  {
    @Typed( { Foo.class, Runnable.class, Callable.class } )
    default Foo provideX()
    {
      return null;
    }
  }
}
