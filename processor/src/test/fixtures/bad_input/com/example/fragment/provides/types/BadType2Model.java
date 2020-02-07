package com.example.fragment.provides.types;

import java.util.concurrent.Callable;
import sting.Fragment;
import sting.Typed;

@Fragment
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

  @Typed( { Foo.class, Runnable.class, Callable.class } )
  default Foo provideX()
  {
    return null;
  }
}
