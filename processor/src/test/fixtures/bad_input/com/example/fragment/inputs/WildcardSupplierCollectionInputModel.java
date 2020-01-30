package com.example.fragment.inputs;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Fragment;

@Fragment
public interface WildcardSupplierCollectionInputModel
{
  default String provideX( Collection<Supplier<?>> supplier )
  {
    return null;
  }
}
