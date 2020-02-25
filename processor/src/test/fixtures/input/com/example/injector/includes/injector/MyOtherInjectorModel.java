package com.example.injector.includes.injector;

import sting.Injector;

@Injector( injectable = true, includes = MyFragment.class )
public interface MyOtherInjectorModel
{
  MyModel getMyModel();
}
