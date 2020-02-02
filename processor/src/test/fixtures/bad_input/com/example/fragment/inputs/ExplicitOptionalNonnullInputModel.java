package com.example.fragment.inputs;

import javax.annotation.Nonnull;
import sting.Dependency;
import sting.Fragment;
import sting.NecessityType;

@Fragment
public interface ExplicitOptionalNonnullInputModel
{
  default String provideX( @Dependency( necessity = NecessityType.OPTIONAL ) @Nonnull String value )
  {
    return null;
  }
}
