package com.example.fragment.dependency;

import sting.Dependency;
import sting.Fragment;
import sting.NecessityType;

@Fragment
public interface ExplicitOptionalInputModel
{
  default Runnable provideRunnable( @Dependency( necessity = NecessityType.OPTIONAL) String name )
  {
    return null;
  }
}
