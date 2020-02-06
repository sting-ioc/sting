package com.example.fragment.qualifier;

import sting.Fragment;
import sting.Named;
import sting.Provides;
import sting.Service;

@Fragment
public interface EmptyQualifierModel
{
  @Named( "" )
  default Runnable provideRunnable()
  {
    return null;
  }
}
