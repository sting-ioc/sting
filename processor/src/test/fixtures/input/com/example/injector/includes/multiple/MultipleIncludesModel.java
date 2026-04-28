package com.example.injector.includes.multiple;

import sting.Injector;

@Injector( fragmentOnly = false, includes = { MyFragment.class, MyModel.class } )
interface MultipleIncludesModel
{
  Runnable getRunnable();
}
