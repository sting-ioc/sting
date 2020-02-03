package com.example.injector.outputs;

import sting.Service;
import sting.Injectable;
import sting.Injector;

@Injector
interface IncompatibleTypeOutputModel
{
  @Service( type = Runnable.class )
  MyModel1 getMyModel1();

  @Injectable
  class MyModel1
  {
  }
}
