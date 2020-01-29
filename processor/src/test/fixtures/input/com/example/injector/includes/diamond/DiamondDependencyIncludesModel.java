package com.example.injector.includes.diamond;

import sting.Injector;

@Injector( includes = { MyModel.class, MyFragment1.class, MyFragment2.class } )
interface DiamondDependencyIncludesModel
{
  MyModel getRunnable();
}
