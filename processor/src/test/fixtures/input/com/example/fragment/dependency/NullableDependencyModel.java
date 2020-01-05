package com.example.fragment.dependency;

import javax.annotation.Nullable;
import sting.Fragment;

@Fragment
public interface NullableDependencyModel
{
  default Runnable provideRunnable( @Nullable String name )
  {
    return null;
  }
}
