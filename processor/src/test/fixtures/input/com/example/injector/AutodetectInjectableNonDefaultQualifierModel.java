package com.example.injector;

import sting.Injectable;
import sting.Injector;
import sting.Named;

interface AutodetectInjectableNonDefaultQualifierModel
{
  @Injector
  interface MyInjector
  {
    // The file has a binary descriptor but
    @Named( "BadQualifier" )
    MyModel1 getMyModel1();
  }

  @Named( "BadQualifier" )
  @Injectable
  class MyModel1
  {
  }
}
