package com.example.injector.outputs;

import sting.Injectable;
import sting.Injector;
import sting.Typed;

@Injector
interface TypedOutputModel
{
  @Typed( MyModel.class )
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
