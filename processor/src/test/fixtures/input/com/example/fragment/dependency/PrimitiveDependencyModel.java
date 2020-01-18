package com.example.fragment.dependency;

import sting.Fragment;

@Fragment
public interface PrimitiveDependencyModel
{
  default Runnable provideRunnable( int priority )
  {
    return null;
  }
}
