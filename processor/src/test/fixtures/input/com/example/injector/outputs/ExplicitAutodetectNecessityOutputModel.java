package com.example.injector.outputs;

import sting.Injectable;
import sting.Injector;
import sting.NecessityType;
import sting.Service;

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
