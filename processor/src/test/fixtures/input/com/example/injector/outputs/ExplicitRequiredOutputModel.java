package com.example.injector.outputs;

import sting.Dependency;
import sting.Injectable;
import sting.Injector;
import sting.NecessityType;

@Injector
interface ExplicitRequiredOutputModel
{
  @Dependency( necessity = NecessityType.REQUIRED )
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
