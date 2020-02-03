package com.example.fragment.dependency;

import sting.Service;
import sting.Fragment;

@Fragment
public interface QualifiedDependencyModel
{
  default Runnable provideRunnable( @Service( qualifier = "threadName" ) String name )
  {
    return null;
  }
}
