package com.example.injector.includes.provider.naming.compound;

import sting.Injector;

@Injector( includes = { MyModel1.class, Outer.Middle.Leaf.MyModel2.class } )
interface MyInjector
{
  MyModel1 getMyModel1();

  Outer.Middle.Leaf.MyModel2 getMyModel2();
}
