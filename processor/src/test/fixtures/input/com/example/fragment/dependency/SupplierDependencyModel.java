package com.example.fragment.dependency;

import java.util.function.Supplier;
import sting.Fragment;

@Fragment
public interface SupplierDependencyModel
{
  default Runnable provideRunnable( Supplier<String> name )
  {
    return null;
  }
}
