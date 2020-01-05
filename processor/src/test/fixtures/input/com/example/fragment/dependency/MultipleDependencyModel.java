package com.example.fragment.dependency;

import sting.Fragment;

@Fragment
public interface MultipleDependencyModel
{
  default Runnable provideRunnable( String name, int priority )
  {
    return null;
  }
}
