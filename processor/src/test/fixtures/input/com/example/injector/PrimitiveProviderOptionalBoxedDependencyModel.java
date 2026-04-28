package com.example.injector;

import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector
interface PrimitiveProviderOptionalBoxedDependencyModel
{
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
    MyModel( @Nullable final Integer value )
    {
    }
  }

  @Fragment
  interface MyFragment
  {
    default int provideValue()
    {
      return 23;
    }
  }
}
