package com.example.injector;

import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injector;

@Injector
interface NullableBoxedProviderPrimitiveOutputModel
{
  int getValue();

  @Fragment
  interface MyFragment
  {
    @Nullable
    default Integer provideValue()
    {
      return null;
    }
  }
}
