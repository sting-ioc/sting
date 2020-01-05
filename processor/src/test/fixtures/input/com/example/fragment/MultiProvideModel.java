package com.example.fragment;

import java.io.Serializable;
import java.util.EventListener;
import sting.Fragment;

@Fragment
public interface MultiProvideModel
{
  default Runnable provideRunnable()
  {
    return null;
  }

  default EventListener provideEventListener()
  {
    return null;
  }

  default Serializable provideSerializable()
  {
    return null;
  }
}
