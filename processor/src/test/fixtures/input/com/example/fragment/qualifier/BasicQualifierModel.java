package com.example.fragment.qualifier;

import sting.Fragment;
import sting.Provides;
import sting.Service;

@Fragment
public interface BasicQualifierModel
{
  @Provides( services = @Service( qualifier = "com.bix/SomeQualifier" ) )
  default Runnable provideRunnable()
  {
    return null;
  }
}
