package com.example.injector.outputs;

import sting.Dependency;
import sting.Injectable;
import sting.Injector;
import sting.NecessityType;

@Injector
interface ExplicitAutodetectNecessityOutputModel
{
  @Dependency( necessity = NecessityType.AUTODETECT )
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
