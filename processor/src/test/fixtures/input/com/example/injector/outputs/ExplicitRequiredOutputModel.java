package com.example.injector.outputs;

import sting.Injectable;
import sting.Injector;
import sting.NecessityType;
import sting.Service;

@Injector
interface ExplicitRequiredOutputModel
{
  @Service( necessity = NecessityType.REQUIRED )
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
