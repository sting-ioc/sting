package com.example.fragment.dependency;

import sting.Fragment;
import sting.NecessityType;
import sting.Service;

@Fragment
public interface ExplicitAutodetectNecessityInputModel
{
  default Runnable provideRunnable( @Service( necessity = NecessityType.AUTODETECT ) String name )
  {
    return null;
  }
}
