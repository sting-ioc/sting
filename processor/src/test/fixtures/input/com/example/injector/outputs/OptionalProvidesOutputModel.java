package com.example.injector.outputs;

import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injector;

@Injector
interface OptionalProvidesOutputModel
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
