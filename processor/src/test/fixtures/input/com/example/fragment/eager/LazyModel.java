package com.example.fragment.eager;

import sting.Fragment;
import sting.Provides;

@Fragment
public interface LazyModel
{
  @SuppressWarnings( "DefaultAnnotationParam" )
  @Provides( eager = false )
  default Runnable provideRunnable()
  {
    return null;
  }
}
