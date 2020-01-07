package com.example.fragment.id;

import sting.Fragment;
import sting.Provides;

@Fragment
public interface ExplicitIdModel
{
  @Provides( id = "provideZeRunnable" )
  default Runnable provideRunnable()
  {
    return null;
  }
}
