package com.example.fragment.provides;

import javax.inject.Singleton;
import sting.Fragment;

@Fragment
public interface Jsr330ScopedProvidesModel
{
  @Singleton
  default Runnable provideRunnable()
  {
    return null;
  }
}
