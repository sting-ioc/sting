package com.example.fragment.includes;

import sting.Fragment;

@Fragment
public interface Included1Model
{
  default Runnable provideRunnable()
  {
    return null;
  }
}
