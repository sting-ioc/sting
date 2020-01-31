package com.example.injector.outputs;

import sting.Dependency;
import sting.Injectable;
import sting.Injector;

@Injector
interface IncompatibleTypeOutputModel
{
  @Dependency( type = Runnable.class )
  MyModel1 getMyModel1();

  @Injectable
  class MyModel1
  {
  }
}
