package com.example.injector.outputs;

import javax.annotation.Nullable;
import sting.Service;
import sting.Injectable;
import sting.Injector;
import sting.NecessityType;

@Injector
interface ExplicitAutodetectNecessityNullableOutputModel
{
  @Service( necessity = NecessityType.AUTODETECT )
  @Nullable
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
