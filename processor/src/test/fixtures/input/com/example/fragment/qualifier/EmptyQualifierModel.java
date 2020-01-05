package com.example.fragment.qualifier;

import sting.Fragment;
import sting.Provides;

@Fragment
public interface EmptyQualifierModel
{
  @Provides( qualifier = "" )
  default Runnable provideRunnable()
  {
    return null;
  }
}
