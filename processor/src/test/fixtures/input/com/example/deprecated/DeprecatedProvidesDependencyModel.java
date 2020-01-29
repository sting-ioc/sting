package com.example.deprecated;

import sting.Fragment;

@Fragment
public interface DeprecatedProvidesDependencyModel
{
  @SuppressWarnings( "DeprecatedIsStillUsed" )
  @Deprecated
  class MyDeprecatedValue
  {
  }

  default Runnable provideRunnable( MyDeprecatedValue value )
  {
    return null;
  }
}
