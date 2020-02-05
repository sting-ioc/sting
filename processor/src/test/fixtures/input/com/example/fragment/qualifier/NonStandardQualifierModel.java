package com.example.fragment.qualifier;

import sting.Fragment;
import sting.Provides;
import sting.Service;

@Fragment
public interface NonStandardQualifierModel
{
  @Provides( services = @Service( qualifier = "\u200E\uD83C\uDF89 Tada!" ) )
  default Runnable provideRunnable()
  {
    return null;
  }
}
