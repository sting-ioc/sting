package com.example.fragment.provides.dependency;

import java.util.Collection;
import sting.Fragment;

@Fragment
public interface WildcardCollectionDependencyModel
{
  default String provideX( Collection<?> collection )
  {
    return null;
  }
}
