package com.example.injector.outputs;

import javax.annotation.Nullable;
import sting.Dependency;
import sting.Injectable;
import sting.Injector;
import sting.NecessityType;

@Injector
interface ExplicitAutodetectNecessityNullableOutputModel
{
  @Dependency( necessity = NecessityType.AUTODETECT )
  @Nullable
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
