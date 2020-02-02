package com.example.fragment.dependency;

import javax.annotation.Nullable;
import sting.Dependency;
import sting.Fragment;
import sting.NecessityType;

@Fragment
public interface ExplicitAutodetectNecessityNullableInputModel
{
  default Runnable provideRunnable( @Dependency( necessity = NecessityType.AUTODETECT ) @Nullable String name )
  {
    return null;
  }
}
