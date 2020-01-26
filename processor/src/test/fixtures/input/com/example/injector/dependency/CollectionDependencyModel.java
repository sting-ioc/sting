package com.example.injector.dependency;

import java.util.Collection;
import sting.Injectable;
import sting.Injector;

@Injector
interface CollectionDependencyModel
{
  Collection<MyModel> getMyModel();

  @Injectable
  class MyModel
  {
  }
}
