package com.example.injector.outputs;

import sting.Service;
import sting.Injectable;
import sting.Injector;
import sting.NecessityType;

@Injector
interface ExplicitAutodetectNecessityOutputModel
{
  @Service( necessity = NecessityType.AUTODETECT )
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
