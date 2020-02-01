package com.example.injector.outputs;

import java.util.Collection;
import sting.Injectable;
import sting.Injector;

@Injector
interface CollectionOutputModel
{
  Collection<MyModel> getMyModel();

  @Injectable
  class MyModel
  {
  }
}
