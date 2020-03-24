package com.example.fragment;

import sting.Fragment;

@Fragment
public interface EnclosedEnumFragmentModel
{
  default Runnable provideRunnable()
  {
    return null;
  }

  enum Foo
  {
  }
}
