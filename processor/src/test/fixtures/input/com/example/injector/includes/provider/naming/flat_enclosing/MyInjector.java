package com.example.injector.includes.provider.naming.flat_enclosing;

import sting.Injector;

@Injector( fragmentOnly = false, includes = { MyModel.class, Outer.Middle.Leaf.MyModel2.class } )
interface MyInjector
{
  MyModel getMyModel();

  Outer.Middle.Leaf.MyModel2 getMyModel2();
}
