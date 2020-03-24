package com.example.deprecated;

import sting.Fragment;

public interface DeprecatedProvidesDependencyModel
{
  @SuppressWarnings( "DeprecatedIsStillUsed" )
  @Deprecated
  class MyDeprecatedValue
  {
  }

  @Fragment
  interface MyFragment
  {
    default Runnable provideRunnable( MyDeprecatedValue value )
    {
      return null;
    }
  }
}
