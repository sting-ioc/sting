package com.example.fragment.inputs;

import sting.Dependency;
import sting.Fragment;
import sting.NecessityType;

@Fragment
public interface ExplicitOptionalPrimitiveInputModel
{
  default String provideX( @Dependency( necessity = NecessityType.OPTIONAL ) int value )
  {
    return null;
  }
}
