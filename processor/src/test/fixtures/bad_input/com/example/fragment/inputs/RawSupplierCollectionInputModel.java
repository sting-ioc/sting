package com.example.fragment.inputs;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Fragment;

@Fragment
public interface RawSupplierCollectionInputModel
{
  @SuppressWarnings( "rawtypes" )
  default String provideX( Collection<Supplier> supplier )
  {
    return null;
  }
}
