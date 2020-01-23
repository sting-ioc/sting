package com.example.fragment.provides.dependency;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import sting.Fragment;

@Fragment
public interface RawParameterizedSupplierCollectionDependencyModel
{
  @SuppressWarnings( "rawtypes" )
  default String provideX( Collection<Supplier<List>> supplier )
  {
    return null;
  }
}
