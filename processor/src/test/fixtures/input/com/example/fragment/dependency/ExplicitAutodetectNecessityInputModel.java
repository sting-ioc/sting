package com.example.fragment.dependency;

import sting.Service;
import sting.Fragment;
import sting.NecessityType;

@Fragment
public interface ExplicitAutodetectNecessityInputModel
{
  default Runnable provideRunnable( @Service( necessity = NecessityType.AUTODETECT ) String name )
  {
    return null;
  }
}
