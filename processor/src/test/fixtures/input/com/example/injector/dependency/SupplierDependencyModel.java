package com.example.injector.dependency;

import java.util.function.Supplier;
import sting.Injectable;
import sting.Injector;

@Injector
abstract class SupplierDependencyModel
{
  abstract Supplier<MyModel> getMyModel();

  @Injectable
  static class MyModel
  {
  }
}
