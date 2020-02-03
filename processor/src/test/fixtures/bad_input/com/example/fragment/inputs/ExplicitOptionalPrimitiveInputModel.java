package com.example.fragment.inputs;

import sting.Service;
import sting.Fragment;
import sting.NecessityType;

@Fragment
public interface ExplicitOptionalPrimitiveInputModel
{
  default String provideX( @Service( necessity = NecessityType.OPTIONAL ) int value )
  {
    return null;
  }
}
