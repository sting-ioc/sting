package com.example.injector.outputs;

import sting.Dependency;
import sting.Injectable;
import sting.Injector;
import sting.NecessityType;

@Injector
interface ExplicitOptionalOutputModel
{
  @Dependency( necessity = NecessityType.OPTIONAL )
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
