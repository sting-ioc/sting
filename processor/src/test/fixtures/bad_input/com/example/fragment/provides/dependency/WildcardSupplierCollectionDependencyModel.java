package com.example.fragment.provides.dependency;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Fragment;

@Fragment
public interface WildcardSupplierCollectionDependencyModel
{
  default String provideX( Collection<Supplier<?>> supplier )
  {
    return null;
  }
}
