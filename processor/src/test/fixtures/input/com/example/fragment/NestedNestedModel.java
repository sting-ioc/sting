package com.example.fragment;

import sting.Fragment;

public class NestedNestedModel
{
  public static class Middle
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
}
