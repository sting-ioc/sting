package com.example.injector.outputs;

import java.util.Collection;
import sting.Injector;

@Injector
interface EmptyCollectionOutputModel
{
  Collection<MyModel> getMyModel();

  class MyModel
  {
  }
}
