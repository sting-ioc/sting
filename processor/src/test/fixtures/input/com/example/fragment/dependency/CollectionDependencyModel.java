package com.example.fragment.dependency;

import java.util.Collection;
import sting.Fragment;

@Fragment
public interface CollectionDependencyModel
{
  default Runnable provideRunnable( Collection<String> name )
  {
    return null;
  }
}
