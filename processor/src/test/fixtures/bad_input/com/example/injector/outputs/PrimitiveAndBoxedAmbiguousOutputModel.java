package com.example.injector.outputs;

import sting.Fragment;
import sting.Injector;

@Injector
interface PrimitiveAndBoxedAmbiguousOutputModel
{
  Integer getValue();

  @Fragment
  interface MyFragment1
  {
    default int provideValue()
    {
      return 1;
    }
  }

  @Fragment
  interface MyFragment2
  {
    default Integer provideValue()
    {
      return 2;
    }
  }
}
