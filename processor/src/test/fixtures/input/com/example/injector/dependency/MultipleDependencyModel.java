package com.example.injector.dependency;

import java.util.function.Supplier;
import sting.Injectable;
import sting.Injector;

@Injector
abstract class MultipleDependencyModel
{
  abstract MyModel1 getMyModel();

  abstract Supplier<MyModel2> getMyModelSupplier();

  @Injectable
  static class MyModel1
  {
  }

  @Injectable
  static class MyModel2
  {
  }
}
