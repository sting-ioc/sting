package com.example.fragment.provides.dependency;

import java.util.Collection;
import java.util.List;
import sting.Fragment;

@Fragment
public interface RawParameterizedCollectionDependencyModel
{
  @SuppressWarnings( "rawtypes" )
  default String provideX( Collection<List> supplier )
  {
    return null;
  }
}
