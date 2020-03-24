package com.example.fragment;

import sting.Fragment;

@Fragment
public interface EnclosedInterfaceFragmentModel
{
  default Runnable provideRunnable()
  {
    return null;
  }

  interface Foo
  {
  }
}
