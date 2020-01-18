package com.example.injector.dependency;

import java.util.Collection;
import sting.Injectable;
import sting.Injector;

@Injector
abstract class CollectionDependencyModel
{
  abstract Collection<MyModel> getMyModel();

  @Injectable
  static class MyModel
  {
  }
}
