package com.example.injector.includes.multiple;

import sting.Injector;

@Injector( includes = { MyFragment.class, MyModel.class } )
interface MultipleIncludesModel
{
  Runnable getRunnable();
}
