package com.example.injector.graphviz;

import sting.Injector;

@Injector
interface NameCollisionInjectorModel
{
  com.example.injector.graphviz.pkg1.MyModel getMyModel1();

  com.example.injector.graphviz.pkg2.MyModel getMyModel2();
}
