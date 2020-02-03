package com.example.fragment.dependency;

import sting.Service;
import sting.Fragment;
import sting.NecessityType;

@Fragment
public interface ExplicitRequiredInputModel
{
  default Runnable provideRunnable( @Service( necessity = NecessityType.REQUIRED ) String name )
  {
    return null;
  }
}
