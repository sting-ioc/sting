package com.example.bad_descriptors.scenario3;

import sting.Injector;

@Injector( includes = MyFragment.class )
abstract class MyInjectorModel
{
  abstract Model2 getModel2();
}
