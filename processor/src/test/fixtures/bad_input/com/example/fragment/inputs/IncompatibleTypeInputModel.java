package com.example.fragment.inputs;

import sting.Dependency;
import sting.Fragment;

@Fragment
public interface IncompatibleTypeInputModel
{
  default String provideX( @Dependency( type = Runnable.class ) String dep )
  {
    return null;
  }
}
