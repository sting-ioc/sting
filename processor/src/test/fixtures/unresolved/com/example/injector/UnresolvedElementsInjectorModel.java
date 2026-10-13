package com.example.injector;

import sting.Injector;

@Injector( fragmentOnly = false, includes = { MyFragment.class, MyModel.class } )
@org.jspecify.annotations.NullMarked
interface UnresolvedElementsInjectorModel
{
  Runnable getRunnable();

  MyModel getMyModel();
}
