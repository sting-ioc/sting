package com.example.injector.outputs;

import sting.Service;
import sting.Injectable;
import sting.Injector;
import sting.NecessityType;

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
