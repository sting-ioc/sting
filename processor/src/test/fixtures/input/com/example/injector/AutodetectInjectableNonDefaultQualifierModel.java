package com.example.injector;

import sting.Injectable;
import sting.Injector;
import sting.Named;

interface AutodetectInjectableNonDefaultQualifierModel
{
  @Injector
  interface MyInjector
  {
    @Named( "MyQualifier" )
    MyModel1 getMyModel1();
  }

  @Named( "MyQualifier" )
  @Injectable
  class MyModel1
  {
  }
}
