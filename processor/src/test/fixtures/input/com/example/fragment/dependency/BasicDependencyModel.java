package com.example.fragment.dependency;

import sting.Fragment;

@Fragment
public interface BasicDependencyModel
{
  default Runnable provideRunnable( String name )
  {
    return null;
  }
}
