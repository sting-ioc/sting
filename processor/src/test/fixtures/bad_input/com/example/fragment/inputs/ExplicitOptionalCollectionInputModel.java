package com.example.fragment.inputs;

import java.util.Collection;
import sting.Fragment;
import sting.NecessityType;
import sting.Service;

@Fragment
public interface ExplicitOptionalCollectionInputModel
{
  default String provideX( @Service( necessity = NecessityType.OPTIONAL ) Collection<String> collection )
  {
    return null;
  }
}
