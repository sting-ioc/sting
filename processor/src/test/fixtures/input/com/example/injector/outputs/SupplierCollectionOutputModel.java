package com.example.injector.outputs;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Injectable;
import sting.Injector;

@Injector
interface SupplierCollectionOutputModel
{
  Collection<Supplier<MyModel>> getMyModel();

  @Injectable
  class MyModel
  {
  }
}
