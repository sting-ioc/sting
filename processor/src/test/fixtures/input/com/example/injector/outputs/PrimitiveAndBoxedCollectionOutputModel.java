package com.example.injector.outputs;

import java.util.Collection;
import sting.Fragment;
import sting.Injector;

@Injector
interface PrimitiveAndBoxedCollectionOutputModel
{
  Collection<Integer> getValues();

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
