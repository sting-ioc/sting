package com.example.injector.outputs;

import sting.Injectable;
import sting.Injector;
import sting.NecessityType;
import sting.Service;

@Injector
interface ExplicitOptionalOutputModel
{
  @Service( necessity = NecessityType.OPTIONAL )
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
