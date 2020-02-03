package com.example.fragment.dependency;

import javax.annotation.Nullable;
import sting.Service;
import sting.Fragment;
import sting.NecessityType;

@Fragment
public interface ExplicitAutodetectNecessityNullableInputModel
{
  default Runnable provideRunnable( @Service( necessity = NecessityType.AUTODETECT ) @Nullable String name )
  {
    return null;
  }
}
