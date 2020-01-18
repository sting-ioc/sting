package com.example.fragment.provides.dependency;

import java.util.Collection;
import sting.Fragment;

@Fragment
public interface RawCollectionDependencyModel
{
  @SuppressWarnings( "rawtypes" )
  default String provideX( Collection collection )
  {
    return null;
  }
}
