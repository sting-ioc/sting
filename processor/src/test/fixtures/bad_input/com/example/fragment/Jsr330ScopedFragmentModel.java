package com.example.fragment;

import javax.inject.Singleton;
import sting.Fragment;

@SuppressWarnings( "CdiManagedBeanInconsistencyInspection" )
@Fragment
@Singleton
public interface Jsr330ScopedFragmentModel
{
  default Runnable provideRunnable()
  {
    return null;
  }
}
