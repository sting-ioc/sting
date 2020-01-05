package com.example.fragment;

import javax.annotation.Nullable;
import sting.Fragment;

@Fragment
public interface NullableProvidesModel
{
  @Nullable
  default Runnable provideRunnable()
  {
    return null;
  }
}
