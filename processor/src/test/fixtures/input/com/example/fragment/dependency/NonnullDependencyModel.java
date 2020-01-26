package com.example.fragment.dependency;

import javax.annotation.Nonnull;
import sting.Fragment;

@Fragment
public interface NonnullDependencyModel
{
  default Runnable provideRunnable( @Nonnull String name )
  {
    return null;
  }
}
