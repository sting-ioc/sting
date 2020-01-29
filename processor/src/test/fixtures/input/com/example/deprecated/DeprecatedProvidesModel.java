package com.example.deprecated;

import sting.Fragment;

@Fragment
public interface DeprecatedProvidesModel
{
  @Deprecated
  default Runnable provideRunnable()
  {
    return null;
  }
}
