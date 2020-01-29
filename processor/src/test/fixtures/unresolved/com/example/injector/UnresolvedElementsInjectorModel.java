package com.example.injector;

import sting.Injector;

@Injector( includes = { MyFragment.class, MyModel.class } )
interface UnresolvedElementsInjectorModel
{
  Runnable getRunnable();

  MyModel getMyModel();
}
