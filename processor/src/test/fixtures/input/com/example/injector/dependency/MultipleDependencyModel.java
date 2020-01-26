package com.example.injector.dependency;

import java.util.function.Supplier;
import sting.Injectable;
import sting.Injector;

@Injector
interface MultipleDependencyModel
{
  MyModel1 getMyModel();

  Supplier<MyModel2> getMyModelSupplier();

  @Injectable
  class MyModel1
  {
  }

  @Injectable
  class MyModel2
  {
  }
}
