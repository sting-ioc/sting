package com.example.injector.outputs;

import java.util.function.Supplier;
import sting.Injectable;
import sting.Injector;

@Injector
interface MultipleOutputModel
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
