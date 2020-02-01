package com.example.injector.outputs;

import javax.annotation.Nullable;
import sting.Injectable;
import sting.Injector;

@Injector
interface OptionalOutputModel
{
  @Nullable
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
