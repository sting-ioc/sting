package com.example.injector.circular;

import java.util.function.Supplier;
import sting.Injectable;
import sting.Injector;

@Injector
interface SupplierBrokenChainedCircularDependencyModel
{
  MyModel1 getMyModel1();

  MyModel2 getMyModel2();

  MyModel3 getMyModel3();

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
    MyModel2( MyModel3 model )
    {
    }
  }

  @Injectable
  class MyModel3
  {
    MyModel3( Supplier<MyModel1> model )
    {
    }
  }
}
