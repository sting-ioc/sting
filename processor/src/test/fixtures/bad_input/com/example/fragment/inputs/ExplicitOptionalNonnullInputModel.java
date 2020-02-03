package com.example.fragment.inputs;

import javax.annotation.Nonnull;
import sting.Service;
import sting.Fragment;
import sting.NecessityType;

@Fragment
public interface ExplicitOptionalNonnullInputModel
{
  default String provideX( @Service( necessity = NecessityType.OPTIONAL ) @Nonnull String value )
  {
    return null;
  }
}
