package com.example.fragment.eager;

import sting.Fragment;
import sting.Provides;

@Fragment
public interface EagerModel
{
  @Provides( eager = true )
  default Runnable provideRunnable()
  {
    return null;
  }
}
