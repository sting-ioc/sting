package com.example.deprecated;

import sting.Fragment;

@Fragment
@Deprecated
public interface DeprecatedFragmentModel
{
  default Runnable provideRunnable()
  {
    return null;
  }
}
