package com.example.injector.circular;

import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector
interface ChainedCircularDependencyModel
{
  MyModel1 getMyModel1();

  String getConfig();

  @Nullable
  Integer getInteger();

  MyModel4 getMyModel4();

  @Injectable
  class MyModel1
  {
    MyModel1( String model )
    {
    }
  }

  @Fragment
  interface MyFragment1
  {
    default String provideConfig( @Nullable Integer model )
    {
      return null;
    }
  }

  @Fragment
  interface MyFragment2
  {
    // Nullable provides
    @Nullable
    default Integer provideInteger( MyModel4 model )
    {
      return null;
    }
  }

  @Injectable
  class MyModel4
  {
    MyModel4( MyModel1 model )
    {
    }
  }
}
