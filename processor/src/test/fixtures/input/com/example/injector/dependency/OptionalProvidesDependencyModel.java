package com.example.injector.dependency;

import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injector;

@Injector
interface OptionalProvidesDependencyModel
{
  @Nullable
  MyModel getMyModel();

  @Fragment
  interface MyFragment
  {
    @Nullable
    default MyModel provideValue()
    {
      return null;
    }
  }

  class MyModel
  {
  }
}
