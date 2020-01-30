package com.example.injector.inputs;

import sting.Dependency;
import sting.Injectable;
import sting.Injector;

@Injector
interface IncompatibleTypeInputModel
{
  @Dependency( type = Runnable.class )
  MyModel1 getMyModel1();

  @Injectable
  class MyModel1
  {
  }
}
