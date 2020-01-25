package com.example.fragment.provides.dependency;

import java.util.Collection;
import java.util.List;
import sting.Fragment;

@Fragment
public interface ParameterizedCollectionDependencyModel
{
  default String provideX( Collection<List<String>> supplier )
  {
    return null;
  }
}
