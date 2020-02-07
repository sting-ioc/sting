package com.example.fragment.inputs;

import java.util.function.Supplier;
import sting.Fragment;
import sting.NecessityType;
import sting.Service;

@Fragment
public interface ExplicitOptionalSupplierInputModel
{
  default String provideX( @Service( necessity = NecessityType.OPTIONAL ) Supplier<String> value )
  {
    return null;
  }
}
