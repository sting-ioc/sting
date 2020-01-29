package com.example.injector.includes.single;

import sting.Injector;

@Injector( includes = MyFragment.class )
interface SingleIncludesModel
{
  Runnable getRunnable();
}
