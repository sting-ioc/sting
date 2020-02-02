package com.example.fragment.dependency;

import sting.Dependency;
import sting.Fragment;
import sting.NecessityType;

@Fragment
public interface ExplicitAutodetectNecessityInputModel
{
  default Runnable provideRunnable( @Dependency( necessity = NecessityType.AUTODETECT ) String name )
  {
    return null;
  }
}
