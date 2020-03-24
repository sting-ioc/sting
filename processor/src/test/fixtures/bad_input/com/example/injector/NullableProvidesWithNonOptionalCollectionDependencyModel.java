package com.example.injector;

import java.util.Collection;
import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector
interface NullableProvidesWithNonOptionalCollectionDependencyModel
{
  MyModel1 getMyModel1();

  @Injectable
  class MyModel1
  {
    MyModel1( String config )
    {
    }
  }

  @Fragment
  interface MyFragment1
  {
    default String provideConfig( /* This should be @Nullable annotated */ Collection<Integer> models )
    {
      return null;
    }
  }

  @Fragment
  interface MyFragment2
  {
    // Nullable provides
    @Nullable
    default Integer provideInteger()
    {
      return null;
    }
  }

  @Fragment
  interface MyFragment3
  {
    default Integer provideInteger()
    {
      return null;
    }
  }

  @Fragment
  interface MyFragment4
  {
    // Nullable provides
    @Nullable
    default Integer provideInteger()
    {
      return null;
    }
  }
}
