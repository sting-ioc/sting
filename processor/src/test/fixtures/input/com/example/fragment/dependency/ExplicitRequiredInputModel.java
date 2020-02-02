package com.example.fragment.dependency;

import sting.Dependency;
import sting.Fragment;
import sting.NecessityType;

@Fragment
public interface ExplicitRequiredInputModel
{
  default Runnable provideRunnable( @Dependency( necessity = NecessityType.REQUIRED ) String name )
  {
    return null;
  }
}
