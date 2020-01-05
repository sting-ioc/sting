package com.example.fragment;

import sting.Fragment;

public class NestedModel
{
  @Fragment
  public interface MyModel
  {
    default Runnable provideRunnable()
    {
      return null;
    }
  }
}
