package com.example.fragment;

import sting.Fragment;

@Fragment
public interface EnclosedClassFragmentModel
{
  default Runnable provideRunnable()
  {
    return null;
  }

  class Foo
  {
  }
}
