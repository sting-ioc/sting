package com.example.fragment.dependency;

import sting.Fragment;

@Fragment
public interface PrimitiveDependencyModel
{
  default Runnable provideRunnable( boolean bool, char ch, byte b, short s, int priority, long l, float f, double d )
  {
    return null;
  }
}
