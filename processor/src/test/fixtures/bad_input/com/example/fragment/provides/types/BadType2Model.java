package com.example.fragment.provides.types;

import java.util.concurrent.Callable;
import sting.Fragment;
import sting.Provides;

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

  @Provides( types = { Foo.class, Runnable.class, Object.class, Callable.class } )
  default Foo provideX()
  {
    return null;
  }
}
