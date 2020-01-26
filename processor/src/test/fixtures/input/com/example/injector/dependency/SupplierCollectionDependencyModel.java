package com.example.injector.dependency;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Injectable;
import sting.Injector;

@Injector
interface SupplierCollectionDependencyModel
{
  Collection<Supplier<MyModel>> getMyModel();

  @Injectable
  class MyModel
  {
  }
}
