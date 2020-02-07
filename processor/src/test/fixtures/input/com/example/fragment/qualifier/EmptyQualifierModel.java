package com.example.fragment.qualifier;

import sting.Fragment;
import sting.Named;

@Fragment
public interface EmptyQualifierModel
{
  @Named( "" )
  default Runnable provideRunnable()
  {
    return null;
  }
}
