package com.example.injector.outputs;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Fragment;
import sting.Injector;

@Injector
interface PrimitiveProviderBoxedSupplierKindsOutputModel
{
  Collection<Integer> getValues();

  Supplier<Integer> getValueSupplier();

  Collection<Supplier<Integer>> getValueSuppliers();

  @Fragment
  interface MyFragment
  {
    default int provideValue()
    {
      return 7;
    }
  }
}
