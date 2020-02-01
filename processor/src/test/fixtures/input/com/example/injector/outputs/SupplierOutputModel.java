package com.example.injector.outputs;

import java.util.function.Supplier;
import sting.Injectable;
import sting.Injector;

@Injector
interface SupplierOutputModel
{
  Supplier<MyModel> getMyModel();

  @Injectable
  class MyModel
  {
  }
}
