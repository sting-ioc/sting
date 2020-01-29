package com.example.injector.dependency;

import java.util.Collection;
import sting.Injector;

@Injector
interface EmptyCollectionDependencyModel
{
  Collection<MyModel> getMyModel();

  class MyModel
  {
  }
}
