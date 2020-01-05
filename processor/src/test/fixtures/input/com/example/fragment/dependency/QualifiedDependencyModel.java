package com.example.fragment.dependency;

import sting.Dependency;
import sting.Fragment;

@Fragment
public interface QualifiedDependencyModel
{
  default Runnable provideRunnable( @Dependency( qualifier = "threadName" ) String name )
  {
    return null;
  }
}
