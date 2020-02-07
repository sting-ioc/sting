package com.example.fragment.inputs;

import javax.annotation.Nonnull;
import sting.Fragment;
import sting.NecessityType;
import sting.Service;

@Fragment
public interface ExplicitOptionalNonnullInputModel
{
  default String provideX( @Service( necessity = NecessityType.OPTIONAL ) @Nonnull String value )
  {
    return null;
  }
}
