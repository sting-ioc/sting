package com.example.fragment.eager;

import sting.Eager;
import sting.Fragment;

@Fragment
public interface EagerModel
{
  @Eager
  default Runnable provideRunnable()
  {
    return null;
  }
}
