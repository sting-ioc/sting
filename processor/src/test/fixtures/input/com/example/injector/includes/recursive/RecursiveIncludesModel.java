package com.example.injector.includes.recursive;

import sting.Injector;
import sting.Service;

@Injector( includes = { MyFragment1.class, MyModel1.class } )
interface RecursiveIncludesModel
{
  Runnable getRunnable1();

  @Service( qualifier = "Fragment2" )
  Runnable getRunnable2();

  @Service( qualifier = "Fragment3" )
  Runnable getRunnable3();

  MyModel1 getMyModel1();

  MyModel2 getMyModel2();

  MyModel3 getMyModel3();
}
