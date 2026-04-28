package com.example.injector.outputs;

import sting.Fragment;
import sting.Injector;

@Injector
interface BoxedProviderPrimitiveOutputModel
{
  int getValue();

  @Fragment
  interface MyFragment
  {
    default Integer provideValue()
    {
      return 42;
    }
  }
}
