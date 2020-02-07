package com.example.fragment.dependency;

import javax.annotation.Nullable;
import sting.Fragment;
import sting.NecessityType;
import sting.Service;

@Fragment
public interface ExplicitAutodetectNecessityNullableInputModel
{
  default Runnable provideRunnable( @Service( necessity = NecessityType.AUTODETECT ) @Nullable String name )
  {
    return null;
  }
}
