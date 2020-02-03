package com.example.injector.outputs;

import sting.Service;
import sting.Injectable;
import sting.Injector;
import sting.NecessityType;

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
