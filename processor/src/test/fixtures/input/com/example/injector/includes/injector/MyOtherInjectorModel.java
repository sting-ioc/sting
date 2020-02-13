package com.example.injector.includes.injector;

import sting.Injector;

@Injector( includes = MyFragment.class )
public interface MyOtherInjectorModel
{
  MyModel getMyModel();
}
