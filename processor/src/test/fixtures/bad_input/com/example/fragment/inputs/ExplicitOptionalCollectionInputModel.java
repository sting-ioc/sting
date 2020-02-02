package com.example.fragment.inputs;

import java.util.Collection;
import sting.Dependency;
import sting.Fragment;
import sting.NecessityType;

@Fragment
public interface ExplicitOptionalCollectionInputModel
{
  default String provideX( @Dependency( necessity = NecessityType.OPTIONAL ) Collection<String> collection )
  {
    return null;
  }
}
