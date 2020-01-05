package com.example.fragment.dependency;

import javax.annotation.Nullable;
import sting.Fragment;

@Fragment
public interface PrimitiveDependencyModel
{
  default Runnable provideRunnable( int priority )
  {
    return null;
  }
}
