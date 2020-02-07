package com.example.fragment.dependency;

import sting.Fragment;
import sting.NecessityType;
import sting.Service;

@Fragment
public interface ExplicitOptionalInputModel
{
  default Runnable provideRunnable( @Service( necessity = NecessityType.OPTIONAL) String name )
  {
    return null;
  }
}
