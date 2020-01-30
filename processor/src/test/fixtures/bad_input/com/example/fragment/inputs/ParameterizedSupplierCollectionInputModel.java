package com.example.fragment.inputs;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import sting.Fragment;

@Fragment
public interface ParameterizedSupplierCollectionInputModel
{
  default String provideX( Collection<Supplier<List<String>>> supplier )
  {
    return null;
  }
}
