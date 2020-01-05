package com.example.fragment.qualifier;

import sting.Fragment;
import sting.Provides;

@Fragment
public interface NonStandardQualifierModel
{
  @Provides( qualifier = "\u200E\uD83C\uDF89 Tada!" )
  default Runnable provideRunnable()
  {
    return null;
  }
}
