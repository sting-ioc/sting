package com.example.fragment.provides.dependency;

import java.util.function.Supplier;
import sting.Fragment;

@Fragment
public interface WildcardSupplierDependencyModel
{
  default String provideX( Supplier<?> supplier )
  {
    return null;
  }
}
