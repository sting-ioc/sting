package com.example.fragment.inputs;

import sting.Fragment;

@Fragment
public interface ArrayTypeInputModel
{
  default String provideX( String[] dep )
  {
    return null;
  }
}
