package com.example.fragment.dependency;

import sting.Service;
import sting.Fragment;
import sting.NecessityType;

@Fragment
public interface ExplicitOptionalInputModel
{
  default Runnable provideRunnable( @Service( necessity = NecessityType.OPTIONAL) String name )
  {
    return null;
  }
}
