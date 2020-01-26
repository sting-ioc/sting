package com.example.injector.dependency;

import java.util.function.Supplier;
import sting.Injectable;
import sting.Injector;

@Injector
interface SupplierDependencyModel
{
  Supplier<MyModel> getMyModel();

  @Injectable
  class MyModel
  {
  }
}
