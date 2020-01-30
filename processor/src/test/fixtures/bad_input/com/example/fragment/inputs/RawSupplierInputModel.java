package com.example.fragment.inputs;

import java.util.function.Supplier;
import sting.Fragment;

@Fragment
public interface RawSupplierInputModel
{
  @SuppressWarnings( "rawtypes" )
  default String provideX( Supplier supplier )
  {
    return null;
  }
}
