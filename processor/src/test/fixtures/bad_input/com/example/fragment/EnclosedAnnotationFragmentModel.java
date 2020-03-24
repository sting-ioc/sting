package com.example.fragment;

import sting.Fragment;

@Fragment
public interface EnclosedAnnotationFragmentModel
{
  default Runnable provideRunnable()
  {
    return null;
  }

  @interface Foo
  {
  }
}
