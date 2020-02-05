package com.example.fragment.qualifier;

import sting.Fragment;
import sting.Provides;
import sting.Service;

@Fragment
public interface EmptyQualifierModel
{
  @Provides( services = @Service( qualifier = "" ) )
  default Runnable provideRunnable()
  {
    return null;
  }
}
