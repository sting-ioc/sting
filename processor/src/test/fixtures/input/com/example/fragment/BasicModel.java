package com.example.fragment;

import sting.Fragment;

@Fragment
public interface BasicModel
{
  default Runnable provideRunnable()
  {
    return null;
  }
}
