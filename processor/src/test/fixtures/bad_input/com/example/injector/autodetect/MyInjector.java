package com.example.injector.autodetect;

import sting.Injector;
import sting.Named;

@Injector
interface MyInjector
{
  // The file has a binary discriptor but
  @Named( "BadQualifier" )
  MyModel1 getMyModel1();
}

