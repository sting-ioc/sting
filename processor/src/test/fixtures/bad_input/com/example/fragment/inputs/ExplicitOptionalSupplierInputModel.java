package com.example.fragment.inputs;

import java.util.function.Supplier;
import sting.Dependency;
import sting.Fragment;
import sting.NecessityType;

@Fragment
public interface ExplicitOptionalSupplierInputModel
{
  default String provideX( @Dependency( necessity = NecessityType.OPTIONAL ) Supplier<String> value )
  {
    return null;
  }
}
