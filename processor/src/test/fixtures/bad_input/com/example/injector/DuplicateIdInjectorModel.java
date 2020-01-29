package com.example.injector;

import sting.Fragment;
import sting.Injector;
import sting.Provides;

@Injector
interface DuplicateIdInjectorModel
{
  Runnable getFooAsRunnable();

  String getFooAsString();

  @Fragment
  interface MyFragment1
  {
    @Provides( id = "foo" )
    default Runnable provideFoo()
    {
      return null;
    }
  }

  @Fragment
  interface MyFragment2
  {
    @Provides( id = "foo" )
    default String provideFoo()
    {
      return null;
    }
  }
}
