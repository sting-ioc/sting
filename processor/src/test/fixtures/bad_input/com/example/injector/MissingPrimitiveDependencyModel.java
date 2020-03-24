package com.example.injector;

import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector
interface MissingPrimitiveDependencyModel
{
  MyModel1 getMyModel1();

  @Injectable
  class MyModel1
  {
    MyModel1( @Nullable String config )
    {
    }
  }

  @Fragment
  interface MyFragment
  {
    // Nullable provides
    @Nullable
    default String provideConfig( int number )
    {
      return null;
    }
  }
}
