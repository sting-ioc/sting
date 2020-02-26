package com.example.injector;

import sting.Injectable;
import sting.Injector;
import sting.Named;

interface AutodetectInjectableNonDefaultQualifierModel
{
  @Injector
  interface MyInjector
  {
    @Named( "BadQualifier" )
    MyModel1 getMyModel1();
  }

  @Named( "BadQualifier" )
  @Injectable
  class MyModel1
  {
  }
}
