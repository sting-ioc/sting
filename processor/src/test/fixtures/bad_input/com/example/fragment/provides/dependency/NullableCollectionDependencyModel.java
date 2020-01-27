package com.example.fragment.provides.dependency;

import java.util.Collection;
import javax.annotation.Nullable;
import sting.Fragment;

@Fragment
public interface NullableCollectionDependencyModel
{
  default String provideX( @Nullable Collection<String> collection )
  {
    return null;
  }
}
