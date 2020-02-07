package com.example.fragment.dependency;

import sting.Fragment;
import sting.NecessityType;
import sting.Service;

@Fragment
public interface ExplicitRequiredInputModel
{
  default Runnable provideRunnable( @Service( necessity = NecessityType.REQUIRED ) String name )
  {
    return null;
  }
}
