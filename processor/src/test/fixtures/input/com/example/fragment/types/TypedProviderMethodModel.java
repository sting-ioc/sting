package com.example.fragment.types;

import sting.Fragment;
import sting.Typed;

@Fragment
public interface TypedProviderMethodModel
{
  @Typed( Runnable.class )
  default MyModel provideMyModel()
  {
    return null;
  }
}
