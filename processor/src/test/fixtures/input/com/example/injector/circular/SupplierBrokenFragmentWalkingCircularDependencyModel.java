package com.example.injector.circular;

import java.util.function.Supplier;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector
interface SupplierBrokenFragmentWalkingCircularDependencyModel
{
  MyModel1 getMyModel1();

  MyModel2 getMyModel2();

  Runnable getRunnable();

  @Injectable
  class MyModel1
  {
    MyModel1( MyModel2 model )
    {
    }
  }

  @Injectable
  class MyModel2
  {
    MyModel2( Runnable model )
    {
    }
  }

  @Fragment
  interface MyFragment
  {
    default Runnable provideRunnable( Supplier<MyModel1> model )
    {
      return null;
    }
  }
}
