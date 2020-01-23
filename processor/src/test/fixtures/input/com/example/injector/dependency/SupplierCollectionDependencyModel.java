package com.example.injector.dependency;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Injectable;
import sting.Injector;

@Injector
abstract class SupplierCollectionDependencyModel
{
  abstract Collection<Supplier<MyModel>> getMyModel();

  @Injectable
  static class MyModel
  {
  }
}
