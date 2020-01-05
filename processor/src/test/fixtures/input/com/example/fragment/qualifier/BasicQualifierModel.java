package com.example.fragment.qualifier;

import sting.Fragment;
import sting.Provides;

@Fragment
public interface BasicQualifierModel
{
  @Provides( qualifier = "com.bix/SomeQualifier" )
  default Runnable provideRunnable()
  {
    return null;
  }
}
