package com.example.fragment.dependency;

import sting.Fragment;
import sting.Named;

@Fragment
public interface QualifiedDependencyModel
{
  default Runnable provideRunnable( @Named( "threadName" ) String name )
  {
    return null;
  }
}
