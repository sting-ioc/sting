package com.example.fragment.provides.types;

import java.util.concurrent.Callable;
import sting.Fragment;
import sting.Provides;
import sting.Service;

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

  @Provides( services = { @Service( type = Foo.class ),
                          @Service( type = Runnable.class ),
                          @Service( type = Callable.class ) } )
  default Foo provideX()
  {
    return null;
  }
}
