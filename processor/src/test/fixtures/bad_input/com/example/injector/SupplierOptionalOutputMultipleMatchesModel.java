package com.example.injector;

import java.util.Optional;
import java.util.function.Supplier;
import sting.Fragment;
import sting.Injector;

@Injector
interface SupplierOptionalOutputMultipleMatchesModel
{
  Supplier<Optional<Runnable>> getRunnable();

  @Fragment
  interface MyFragment1
  {
    default Runnable provideRunnable1()
    {
      return null;
    }
  }

  @Fragment
  interface MyFragment2
  {
    default Runnable provideRunnable2()
    {
      return null;
    }
  }
}
