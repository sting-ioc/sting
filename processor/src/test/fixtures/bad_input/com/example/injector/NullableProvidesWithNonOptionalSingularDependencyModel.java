package com.example.injector;

import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector
interface NullableProvidesWithNonOptionalSingularDependencyModel
{
  MyModel1 getMyModel1();

  @Injectable
  class MyModel1
  {
    MyModel1( Runnable model )
    {
    }
  }

  @Fragment
  interface MyFragment1
  {
    default Runnable provideRunnable( /* This should be @Nullable annotated */ String config )
    {
      return null;
    }
  }

  @Fragment
  interface MyFragment2
  {
    // Nullable provides
    @Nullable
    default String provideConfig()
    {
      return null;
    }
  }
}
