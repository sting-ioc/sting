package com.example.fragment.provides.dependency;

import sting.Dependency;
import sting.Fragment;

@Fragment
public interface IncompatibleTypeDependencyModel
{
  default String provideX( @Dependency( type = Runnable.class ) String dep )
  {
    return null;
  }
}
