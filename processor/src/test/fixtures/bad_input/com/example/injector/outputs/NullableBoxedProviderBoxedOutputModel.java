package com.example.injector.outputs;

import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injector;

@Injector
interface NullableBoxedProviderBoxedOutputModel
{
  Integer getValue();

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
