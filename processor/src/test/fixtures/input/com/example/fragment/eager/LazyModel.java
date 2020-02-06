package com.example.fragment.eager;

import sting.Fragment;

@Fragment
public interface LazyModel
{
  default Runnable provideRunnable()
  {
    return null;
  }
}
