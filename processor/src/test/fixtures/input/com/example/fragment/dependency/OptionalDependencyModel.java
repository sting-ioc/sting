package com.example.fragment.dependency;

import java.util.Optional;
import sting.Fragment;

@Fragment
public interface OptionalDependencyModel
{
  default Runnable provideRunnable( Optional<String> name )
  {
    return null;
  }
}
