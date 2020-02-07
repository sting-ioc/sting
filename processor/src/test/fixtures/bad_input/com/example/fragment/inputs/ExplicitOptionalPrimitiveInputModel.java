package com.example.fragment.inputs;

import sting.Fragment;
import sting.NecessityType;
import sting.Service;

@Fragment
public interface ExplicitOptionalPrimitiveInputModel
{
  default String provideX( @Service( necessity = NecessityType.OPTIONAL ) int value )
  {
    return null;
  }
}
