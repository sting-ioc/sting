package com.example.fragment.provides.dependency;

import sting.Fragment;

@Fragment
public interface ArrayDependencyModel
{
  default String provideX( String[] dep )
  {
    return null;
  }
}
