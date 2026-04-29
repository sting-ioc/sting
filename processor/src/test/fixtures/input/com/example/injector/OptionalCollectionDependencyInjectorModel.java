package com.example.injector;

import java.util.Collection;
import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector
interface OptionalCollectionDependencyInjectorModel
{
  MyModel getMyModel();

  String getConfig();

  @Injectable
  class MyModel
  {
    MyModel( Collection<Runnable> runnables )
    {
    }
  }

  @Fragment
  interface MyFragment1
  {
    default Runnable provideRunnable1()
    {
      return () -> { };
    }
  }

  @Fragment
  interface MyFragment2
  {
    @Nullable
    default Runnable provideRunnable2()
    {
      return null;
    }
  }

  @Fragment
  interface MyFragment3
  {
    default String provideConfig( Collection<Runnable> runnables )
    {
      return Integer.toString( runnables.size() );
    }
  }
}
