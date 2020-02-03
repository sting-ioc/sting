package com.example.fragment.inputs;

import sting.Service;
import sting.Fragment;

@Fragment
public interface IncompatibleTypeInputModel
{
  default String provideX( @Service( type = Runnable.class ) String dep )
  {
    return null;
  }
}
