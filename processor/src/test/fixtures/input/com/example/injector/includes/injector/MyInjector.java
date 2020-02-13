package com.example.injector.includes.injector;

import sting.Injector;

@Injector( includes = MyOtherInjectorModel.class )
interface MyInjector
{
  MyModel getMyModel();
}
