package com.example.fragment.inputs;

import java.util.Collection;
import sting.Service;
import sting.Fragment;
import sting.NecessityType;

@Fragment
public interface ExplicitOptionalCollectionInputModel
{
  default String provideX( @Service( necessity = NecessityType.OPTIONAL ) Collection<String> collection )
  {
    return null;
  }
}
